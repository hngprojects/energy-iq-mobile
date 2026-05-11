package com.hng14.energyiq

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hng14.energyiq.core.di.appDeclaration
import com.hng14.energyiq.core.navigation.AppDestination
import com.hng14.energyiq.core.navigation.AppNavigation
import com.hng14.energyiq.core.theme.AppTheme
import com.hng14.energyiq.features.auth.data.AuthRepository
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App(context: Any? = null) {
    KoinApplication(
        application = appDeclaration(context = context),
        content = {
            AppTheme(
                content = {
                    Content()
                },
            )
        },
    )
}

@Composable
private fun Content() {
    val auth = koinInject<AuthRepository>()
    var startDestinationKey by rememberSaveable { mutableStateOf(StartDestinationKey.AUTH) }

    LaunchedEffect(Unit) {
        startDestinationKey = if (auth.getCurrentUser() != null) {
            StartDestinationKey.HOME
        } else {
            StartDestinationKey.AUTH
        }
    }

    AppNavigation(
        startDestination = when (startDestinationKey) {
            StartDestinationKey.AUTH -> AppDestination.Auth()
            StartDestinationKey.HOME -> AppDestination.Home
        },
    )
}

private enum class StartDestinationKey {
    AUTH,
    HOME,
}
