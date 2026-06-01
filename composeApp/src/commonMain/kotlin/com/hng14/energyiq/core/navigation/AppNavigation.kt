package com.hng14.energyiq.core.navigation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.presentation.AuthScreen
import com.hng14.energyiq.features.auth.presentation.emailVerification.EmailVerificationScreen
import com.hng14.energyiq.features.alerts.presentation.SmartAlertsScreen
import com.hng14.energyiq.features.chat.presentation.ChatListScreen
import com.hng14.energyiq.features.chat.presentation.ChatScreen
import com.hng14.energyiq.features.home.presentation.HomeScreen
import com.hng14.energyiq.features.onboarding.presentation.InverterSetupScreen
import com.hng14.energyiq.features.onboarding.presentation.OnboardingScreen
import com.hng14.energyiq.core.ui.WebPageScreen
import com.hng14.energyiq.features.chat.data.ChatRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavigation(startDestination: AppDestination) {
    val backStack = remember { mutableStateListOf(startDestination) }
    val auth = koinInject<AuthRepository>()
    val chatRepository = koinInject<ChatRepository>()
    val scope = rememberCoroutineScope()
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
                    onOpenPrivacyPolicy = {
                        backStack.add(
                            AppDestination.WebPage(
                                title = "Privacy Policy",
                                url = "https://staging.energy-iq.hng14.com/privacy-policy",
                            ),
                        )
                    },
                    onOpenTermsAndConditions = {
                        backStack.add(
                            AppDestination.WebPage(
                                title = "Terms & Conditions",
                                url = "https://staging.energy-iq.hng14.com/terms-and-conditions",
                            ),
                        )
                    },
                    onAuthSuccess = { mode, user ->
                        scope.launch {
                            backStack.clear()

                            val nextDestination = when {
                                // Only force the verification screen for sign-up intents if the
                                // returned user is actually unverified. Google sign-in can return
                                // an existing, already-verified account even when the user taps
                                // "Create account".
                                mode == AuthMode.REGISTER && !user.emailVerified -> AppDestination.EmailVerification(
                                    fullName = user.name,
                                    email = user.email,
                                )

                                !user.emailVerified -> AppDestination.EmailVerification(
                                    user.name,
                                    user.email
                                )

                                !user.onBoardingComplete -> AppDestination.InverterSetup
                                else -> AppDestination.Home
                            }
                            backStack.add(nextDestination)
                        }
                    }
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
                    onOpenChat = {
                        backStack.add(AppDestination.ChatEntry)
                    },
                    onLogout = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth())
                    },
                    onOpenInverterSetup = {
                        backStack.add(AppDestination.InverterSetup)
                    },
                )
            }
            entry<AppDestination.HomeProfile> {
                HomeScreen(
                    startOnProfile = true,
                    onOpenChat = {
                        backStack.add(AppDestination.ChatEntry)
                    },
                    onLogout = {
                        backStack.clear()
                        backStack.add(AppDestination.Auth())
                    },
                    onOpenInverterSetup = {
                        backStack.add(AppDestination.InverterSetup)
                    },
                )
            }
            entry<AppDestination.SmartAlerts> {
                val auth = koinInject<AuthRepository>()
                var name by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    name = auth.getCurrentUser()?.name ?: ""
                }
                SmartAlertsScreen(
                    name = name,
                    onInspectAlert = { conversationId ->
                        backStack.add(AppDestination.ChatDetail(conversationId))
                    },
                )
            }
            entry<AppDestination.ChatEntry> {
                // Route user to ChatList if they have history; otherwise open a fresh Chat screen.
                // This avoids showing an empty list for first-time users.
                LaunchedEffect(Unit) {
                    val hasHistory = runCatching { chatRepository.getChats().isNotEmpty() }.getOrDefault(false)
                    backStack.removeLastOrNull() // remove ChatEntry
                    backStack.add(if (hasHistory) AppDestination.ChatList else AppDestination.Chat)
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            entry<AppDestination.ChatList> {
                ChatListScreen(
                    onOpenConversation = { conversationId ->
                        backStack.add(AppDestination.ChatDetail(conversationId))
                    },
                    onNewChat = {
                        backStack.add(AppDestination.Chat)
                    },
                    onOpenProfile = {
                        backStack.add(AppDestination.HomeProfile)
                    },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<AppDestination.Chat> {
                ChatScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onOpenProfile = {
                        backStack.add(AppDestination.HomeProfile)
                    },
                )
            }
            entry<AppDestination.ChatDetail> {
                ChatScreen(
                    onBack = { backStack.removeLastOrNull() },
                    conversationId = it.conversationId,
                    onOpenProfile = {
                        backStack.add(AppDestination.HomeProfile)
                    },
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
                        scope.launch {
                            auth.logout()
                            backStack.clear()
                            backStack.add(AppDestination.Auth(AuthMode.REGISTER))

                        }

                    },
                )
            }

            entry<AppDestination.WebPage> {
                WebPageScreen(
                    title = it.title,
                    url = it.url,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
