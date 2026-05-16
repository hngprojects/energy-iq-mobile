package com.hng14.energyiq.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.presentation.AuthScreen
import com.hng14.energyiq.features.auth.presentation.emailVerification.EmailVerificationScreen
import com.hng14.energyiq.features.home.presentation.HomeScreen
import com.hng14.energyiq.features.onboarding.presentation.InverterSetupScreen
import com.hng14.energyiq.features.onboarding.presentation.OnboardingScreen

@Composable
fun AppNavigation(startDestination: AppDestination) {
    val backStack = remember { mutableStateListOf(startDestination) }
    LaunchedEffect(startDestination) {
        backStack.clear()
        backStack.add(startDestination)
    }

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
                    initialResetToken = it.initialResetToken,
                    onAuthSuccess = { mode, fullName, email ->
                        backStack.clear()
                        backStack.add(
                            when (mode) {
                                AuthMode.REGISTER -> AppDestination.EmailVerification(
                                    fullName = fullName,
                                    email = email,
                                )
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
                )
            }
            entry<AppDestination.Home> {
                HomeScreen(
                )
            }

            entry<AppDestination.EmailVerification> {
                EmailVerificationScreen(
                    fullName = it.fullName,
                    email = it.email,
                    onContinue = {
                        backStack.clear()
                        backStack.add(AppDestination.InverterSetup)
                    },
                    onBackToSignUp = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth(AuthMode.REGISTER))
                    },
                )
            }
        },
    )
}
