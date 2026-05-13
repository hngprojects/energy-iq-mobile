package com.hng14.energyiq

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hng14.energyiq.core.di.appDeclaration
import com.hng14.energyiq.core.navigation.AppDestination
import com.hng14.energyiq.core.navigation.AppNavigation
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
                    Content(
                        incomingLink = incomingLink,
                        incomingLinkId = incomingLinkId,
                    )
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
        startDestination = when {
            resetToken != null -> AppDestination.Auth(
                initialMode = AuthMode.CHECK_MAIL,
                initialResetToken = resetToken,
            )
            auth.getCurrentUser() != null -> AppDestination.Home
            else -> AppDestination.Auth()
        }
        println("$logTag content: startDestination=$startDestination")
    }

    startDestination?.let { destination ->
        AppNavigation(startDestination = destination)
    }
}
