package dev.logickoder.starter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.logickoder.starter.core.di.appDeclaration
import dev.logickoder.starter.core.navigation.AppDestination
import dev.logickoder.starter.core.navigation.AppNavigation
import dev.logickoder.starter.core.theme.AppTheme
import dev.logickoder.starter.features.auth.data.AuthRepository
import dev.logickoder.starter.features.onboarding.data.OnboardingRepository
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
    val onboarding = koinInject<OnboardingRepository>()
    var startDestination by remember(calculation = { mutableStateOf<AppDestination?>(null) })

    LaunchedEffect(
        key1 = Unit,
        block = {
            startDestination = when {
                !onboarding.hasCompletedOnboarding() -> AppDestination.Onboarding
                auth.getCurrentUser() != null -> AppDestination.Home
                else -> AppDestination.Auth()
            }
        },
    )

    startDestination?.let { destination ->
        AppNavigation(startDestination = destination)
    }
}