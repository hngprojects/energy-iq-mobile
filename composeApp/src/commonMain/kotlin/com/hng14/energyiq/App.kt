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
import com.hng14.energyiq.core.theme.AppTheme
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
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