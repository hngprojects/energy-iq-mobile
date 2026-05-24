package com.hng14.energyiq

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hng14.energyiq.core.di.appDeclaration
import com.hng14.energyiq.core.navigation.AppDestination
import com.hng14.energyiq.core.navigation.AppNavigation
import com.hng14.energyiq.core.navigation.GoogleAuthLinkParser
import com.hng14.energyiq.core.navigation.ResetPasswordLinkParser
import com.hng14.energyiq.core.theme.AppTheme
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.AuthMode
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App(
    context: Any? = null,
    incomingLink: String? = null,
    incomingLinkId: Long = 0L,
) {
    KoinApplication(
        application = appDeclaration(context = context),
        content = {
            AppTheme(
                content = {
                    Box(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxSize(),
                    ) {
                        Content(
                            incomingLink = incomingLink,
                            incomingLinkId = incomingLinkId,
                        )
                    }
                },
            )
        },
    )
}

@Composable
private fun Content(
    incomingLink: String?,
    incomingLinkId: Long,
) {
    val logTag = "EnergyIQDeepLink"
    val auth = koinInject<AuthRepository>()
    var startDestination by remember { mutableStateOf<AppDestination?>(null) }

    LaunchedEffect(incomingLinkId) {
        println("$logTag content: incomingLinkId=$incomingLinkId incomingLink=$incomingLink")
        val resetToken = ResetPasswordLinkParser.extractToken(incomingLink)
        val googleAccessToken = GoogleAuthLinkParser.extractAccessToken(incomingLink)
        
        startDestination = when {
            googleAccessToken != null -> {
                val pendingGoogleAuthMode = auth.getPendingGoogleAuthMode()
                val googleSignInSuccess = runCatching {
                    auth.signInWithAccessToken(googleAccessToken)
                }.onFailure { error ->
                    println("$logTag content: google callback sign-in failed=${error.message}")
                }.isSuccess

                auth.clearPendingGoogleAuthMode()

                if (googleSignInSuccess) {
                    if (pendingGoogleAuthMode == AuthMode.REGISTER) {
                        AppDestination.InverterSetup
                    } else {
                        AppDestination.Home
                    }
                } else {
                    AppDestination.Auth()
                }
            }
            resetToken != null -> AppDestination.Auth(
                initialMode = AuthMode.CHECK_MAIL,
                initialResetToken = resetToken,
            )
            else -> {
                val user = auth.getCurrentUser()
                if (user != null) {
                    AppDestination.Home
                } else {
                    AppDestination.Auth()
                }
            }
        }
        println("$logTag content: startDestination=$startDestination")
    }

    if (startDestination == null) {
        Box(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            com.hng14.energyiq.core.ui.EnergyIqBrandMark()
        }
    } else {
        AppNavigation(startDestination = startDestination!!)
    }
}
