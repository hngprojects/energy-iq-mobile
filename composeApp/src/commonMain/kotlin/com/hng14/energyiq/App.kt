package com.hng14.energyiq

import androidx.compose.runtime.Composable
import com.hng14.energyiq.core.di.appDeclaration
import com.hng14.energyiq.core.navigation.AppDestination
import com.hng14.energyiq.core.navigation.AppNavigation
import com.hng14.energyiq.core.theme.AppTheme
import org.koin.compose.KoinApplication

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
    AppNavigation(startDestination = AppDestination.Home)
}
