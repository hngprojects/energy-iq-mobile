package com.hng14.energyiq.features.auth.presentation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.hng14.energyiq.core.network.LocalConfig
import kotlinx.coroutines.CancellationException
import java.security.MessageDigest

@Composable
actual fun rememberGoogleIdTokenLauncher(
    onResult: (Result<String>) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val activity = context as? Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val data: Intent? = result.data
        if (data == null && result.resultCode != Activity.RESULT_OK) {
            // Common case: user dismissed the chooser or pressed back and no intent data returned.
            onResult(Result.failure(CancellationException("Google sign-in cancelled")))
            return@rememberLauncherForActivityResult
        }

        runCatching {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val token = account.idToken?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException(
                    "Google Sign-In returned a null/blank idToken. " +
                        "This usually means you're using the wrong client id. " +
                        "Set GOOGLE_WEB_CLIENT_ID to the Web OAuth client id (not the Android client id).",
                )
            token
        }.fold(
            onSuccess = { token -> onResult(Result.success(token)) },
            onFailure = { e ->
                val enriched = if (e is ApiException) {
                    val code = e.statusCode
                    val codeName = runCatching { GoogleSignInStatusCodes.getStatusCodeString(code) }
                        .getOrNull()
                        .orEmpty()
                    val pkg = context.packageName
                    val sha1 = runCatching { context.signingCertSha1(pkg) }.getOrNull().orEmpty()
                    println(
                        "Google sign-in failure details: code=$code codeName=$codeName pkg=$pkg sha1=$sha1",
                    )
                    // Helpful status codes:
                    // 12501 = user canceled
                    // 10 = DEVELOPER_ERROR (wrong client id, missing SHA-1, etc.)
                    Exception("Google sign-in failed (code=$code): ${e.message}", e)
                } else {
                    e
                }
                onResult(Result.failure(enriched))
            },
        )
    }

    val signInIntent = remember(activity) {
        if (activity == null) return@remember null
        val clientId = LocalConfig.googleWebClientId.trim()
        // Log only prefix/length to avoid leaking full IDs in logs.
        println(
            "Google sign-in config: webClientIdBlank=${clientId.isBlank()} " +
                "len=${clientId.length} prefix=${clientId.take(12)}",
        )
        if (clientId.isBlank()) return@remember null

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(clientId)
            .build()

        GoogleSignIn.getClient(activity, gso).signInIntent
    }

    val googleClient: GoogleSignInClient? = remember(activity) {
        if (activity == null) return@remember null
        val clientId = LocalConfig.googleWebClientId.trim()
        if (clientId.isBlank()) return@remember null
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(clientId)
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    return remember(activity, signInIntent) {
        {
            if (activity == null) {
                onResult(Result.failure(IllegalStateException("Google Sign-In requires an Activity context")))
                return@remember
            }
            // Log on every tap so it's visible even if the intent was memoized earlier.
            val clientId = LocalConfig.googleWebClientId.trim()
            println(
                "Google sign-in config (tap): webClientIdBlank=${clientId.isBlank()} " +
                    "len=${clientId.length} prefix=${clientId.take(12)}",
            )
            if (signInIntent == null) {
                onResult(
                    Result.failure(
                        IllegalStateException(
                            "Missing GOOGLE_WEB_CLIENT_ID. Add it to local.properties to enable Google Sign-In.",
                        ),
                    ),
                )
                return@remember
            }
            println("Google sign-in: launching SignInHubActivity")
            // Force the account chooser to show after a user logs out of the app.
            // Without this, GoogleSignIn can silently reuse the previously selected account.
            val client = googleClient
            if (client != null) {
                client.signOut().addOnCompleteListener {
                    launcher.launch(signInIntent)
                }
            } else {
                launcher.launch(signInIntent)
            }
        }
    }
}

private fun android.content.Context.signingCertSha1(packageName: String): String {
    val pm = packageManager
    val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        val sigs = info.signingInfo?.apkContentsSigners
        sigs ?: emptyArray()
    } else {
        @Suppress("DEPRECATION")
        val info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        @Suppress("DEPRECATION")
        info.signatures ?: emptyArray()
    }

    val sig = signatures.firstOrNull() ?: return ""
    val md = MessageDigest.getInstance("SHA1")
    val digest = md.digest(sig.toByteArray())
    return digest.joinToString(":") { b -> "%02X".format(b) }
}
