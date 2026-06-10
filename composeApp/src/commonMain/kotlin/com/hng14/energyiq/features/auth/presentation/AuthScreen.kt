package com.hng14.energyiq.features.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.hng14.energyiq.core.ui.ServerErrorDialog
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.OnAuthSuccess
import com.hng14.energyiq.features.auth.presentation.components.CheckMailContent
import com.hng14.energyiq.features.auth.presentation.components.EmailSentContent
import com.hng14.energyiq.features.auth.presentation.components.ForgotPasswordContent
import com.hng14.energyiq.features.auth.presentation.components.LoginContent
import com.hng14.energyiq.features.auth.presentation.components.RegisterContent
import com.hng14.energyiq.features.auth.presentation.components.ResetSuccessContent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AuthScreen(
    onAuthSuccess: OnAuthSuccess,
    initialMode: AuthMode = AuthMode.LOGIN,
    initialResetToken: String? = null,
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenTermsAndConditions: () -> Unit = {},
) {
    val viewModel = koinViewModel<AuthViewModel> {
        parametersOf(initialMode, initialResetToken)
    }
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var pendingGoogleMode by remember { mutableStateOf<AuthMode?>(null) }

    val launchGoogleSignIn = rememberGoogleIdTokenLauncher { result ->
        val mode = pendingGoogleMode ?: state.mode
        pendingGoogleMode = null
        result.onSuccess { idToken ->
            println("Google sign-in: idToken received (len=${idToken.length}) mode=$mode")
            viewModel.onGoogleIdToken(
                idToken = idToken,
                requestedMode = mode,
                onSuccess = onAuthSuccess,
            )
        }.onFailure { e ->
            println("Google sign-in: failed mode=$mode error=${e.message}")
            viewModel.onShowGeneralError(e.message ?: "Google sign-in failed. Please try again.")
        }
    }

    LaunchedEffect(initialMode, initialResetToken) {
        viewModel.resetToMode(initialMode, initialResetToken)
    }

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onSnackbarShown()
    }

    LaunchedEffect(state.mode, state.generalError, state.isVerificationRequired) {
        if (state.mode == AuthMode.LOGIN) {
            println("AuthScreen: mode=LOGIN error=${state.generalError != null} isVerificationRequired=${state.isVerificationRequired}")
        }
    }

    val onGoogleAuthClick = remember(scope, launchGoogleSignIn) {
        { mode: AuthMode ->
            scope.launch {
                pendingGoogleMode = mode
                launchGoogleSignIn()
            }
            Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { _ ->
        Box(
//            modifier = androidx.compose.ui.Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
        ) {
            state.generalError?.let { message ->
                ServerErrorDialog(
                    message = message,
                    onDismiss = viewModel::onDismissGeneralError,
                    secondaryText = if (state.mode == AuthMode.LOGIN && state.isVerificationRequired) {
                        "Verify email"
                    } else {
                        null
                    },
                    onSecondary = if (state.mode == AuthMode.LOGIN && state.isVerificationRequired) {
                        {
                            viewModel.onDismissGeneralError()
                            viewModel.onGoToVerification(onSuccess = onAuthSuccess)
                        }
                    } else {
                        null
                    },
                )
            }

            when (state.mode) {
                AuthMode.REGISTER -> RegisterContent(
                    fullName = state.fullName,
                    email = state.email,
                    password = state.password,
                    fullNameError = state.fullNameError,
                    emailError = state.emailError,
                    passwordError = state.passwordError,
                    generalError = state.generalError,
                    isLoading = state.isLoading,
                    onFullNameChange = viewModel::onFullNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onCreateAccount = { viewModel.onSubmit(onSuccess = onAuthSuccess) },
                    onGoogleClick = { onGoogleAuthClick(AuthMode.REGISTER) },
                    onLoginClick = viewModel::onToggleMode,
                    onPrivacyPolicyClick = onOpenPrivacyPolicy,
                    onTermsAndConditionsClick = onOpenTermsAndConditions,
                )

                AuthMode.LOGIN -> LoginContent(
                    email = state.email,
                    password = state.password,
                    rememberMe = state.rememberMe,
                    emailError = state.emailError,
                    passwordError = state.passwordError,
                    isVerificationRequired = state.isVerificationRequired,
                    isLoading = state.isLoading,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onRememberMeChange = viewModel::onRememberMeChange,
                    onSubmit = { viewModel.onSubmit(onSuccess = onAuthSuccess) },
                    onGoToVerification = { viewModel.onGoToVerification(onSuccess = onAuthSuccess) },
                    onToggleMode = viewModel::onToggleMode,
                    onForgotPasswordClick = viewModel::onShowForgotPassword,
                    onGoogleClick = { onGoogleAuthClick(AuthMode.LOGIN) },
                    onPrivacyPolicyClick = onOpenPrivacyPolicy,
                    onTermsAndConditionsClick = onOpenTermsAndConditions,
                )

                AuthMode.FORGOT_PASSWORD -> ForgotPasswordContent(
                    email = state.email,
                    emailError = state.emailError,
                    isLoading = state.isLoading,
                    generalError = state.generalError,
                    onEmailChange = viewModel::onEmailChange,
                    onResendLink = viewModel::onForgotPasswordSubmit,
                    onBackToLogin = viewModel::onBackToLogin,
                )

                AuthMode.EMAIL_SENT -> EmailSentContent(
                    generalError = state.generalError,
                    isLoading = state.isLoading,
                    onResendLink = viewModel::onResendForgotPasswordLink,
                    onBackToLogin = viewModel::onBackToLogin,
                )

                AuthMode.CHECK_MAIL -> CheckMailContent(
                    email = state.email,
                    isEmailReadOnly = state.isResetEmailLocked,
                    emailError = state.emailError,
                    password = state.password,
                    confirmPassword = state.confirmPassword,
                    passwordError = state.passwordError,
                    confirmPasswordError = state.confirmPasswordError,
                    generalError = state.generalError,
                    isLoading = state.isLoading,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onResetPassword = viewModel::onResetPasswordSubmit,
                    onBackToLogin = viewModel::onBackToLogin,
                )

                AuthMode.RESET_SUCCESS -> ResetSuccessContent(
                    onSignIn = viewModel::onBackToLogin,
                )
            }
        }
    }
}
