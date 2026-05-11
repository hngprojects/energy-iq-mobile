package com.hng14.energyiq.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.presentation.AuthScreen
import com.hng14.energyiq.features.auth.presentation.email.EmailVerificationScreen
import com.hng14.energyiq.features.home.presentation.HomeScreen
import com.hng14.energyiq.features.onboarding.presentation.InverterSetupScreen
import com.hng14.energyiq.features.onboarding.presentation.OnboardingScreen

@Composable
fun AppNavigation(startDestination: AppDestination) {
    val backStack = remember { mutableStateListOf(startDestination) }

    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppDestination.Onboarding> {
                OnboardingScreen(
                    onComplete = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth())
                    },
                )
            }
            entry<AppDestination.Auth> {
                AuthScreen(
                    initialMode = it.initialMode,
                    onAuthSuccess = { mode ->
                        backStack.clear()
                        backStack.add(
                            when (mode) {
                                AuthMode.REGISTER -> AppDestination.InverterSetup
                                else -> AppDestination.Home
                            },
                        )
                    },
                )
            }
            entry<AppDestination.InverterSetup> {
                InverterSetupScreen(
                    onComplete = {
                        backStack.clear()
                        backStack.add(AppDestination.Home)
                    },
                    onSignIn = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth(AuthMode.LOGIN))
                    },
                )
            }
            entry<AppDestination.Home> {
                HomeScreen(
                    onLogout = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth())
                    },
                )
            }

            entry<AppDestination.EmailVerification> {
                EmailVerificationScreen(
                    onAction = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth())
                    },
                )
            }
        },
    )
}
