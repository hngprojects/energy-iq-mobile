package com.hng14.energyiq.features.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.OnAuthSuccess
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.presentation.components.CheckMailContent
import com.hng14.energyiq.features.auth.presentation.components.EmailSentContent
import com.hng14.energyiq.features.auth.presentation.components.ForgotPasswordContent
import com.hng14.energyiq.features.auth.presentation.components.LoginContent
import com.hng14.energyiq.features.auth.presentation.components.RegisterContent
import com.hng14.energyiq.features.auth.presentation.components.ResetSuccessContent
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AuthScreen(
    onAuthSuccess: OnAuthSuccess,
    initialMode: AuthMode = AuthMode.LOGIN,
    initialResetToken: String? = null,
) {
    val viewModel = koinViewModel<AuthViewModel> {
        parametersOf(initialMode, initialResetToken)
    }
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val repository = koinInject<AuthRepository>()
    val googleAuthUrl = remember { "${NetworkConfig.BASE_URL}/auth/google" }

    LaunchedEffect(initialMode, initialResetToken) {
        viewModel.resetToMode(initialMode, initialResetToken)
    }

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onSnackbarShown()
    }

    val onGoogleAuthClick = remember(uriHandler, googleAuthUrl, repository, scope) {
        { mode: AuthMode ->
            scope.launch {
                repository.savePendingGoogleAuthMode(mode)
                println("Google auth: opening $googleAuthUrl for mode=$mode")
                runCatching {
                    uriHandler.openUri(googleAuthUrl)
                }.onSuccess {
                    println("Google auth: browser open triggered")
                }.onFailure { error ->
                    println("Google auth: failed to open $googleAuthUrl -> ${error.message}")
                }
            }
            Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Box(
//            modifier = androidx.compose.ui.Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
        ) {
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
                )

                AuthMode.LOGIN -> LoginContent(
                    email = state.email,
                    password = state.password,
                    emailError = state.emailError,
                    passwordError = state.passwordError,
                    generalError = state.generalError,
                    isLoading = state.isLoading,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onSubmit = { viewModel.onSubmit(onSuccess = onAuthSuccess) },
                    onToggleMode = viewModel::onToggleMode,
                    onForgotPasswordClick = viewModel::onShowForgotPassword,
                    onGoogleClick = { onGoogleAuthClick(AuthMode.LOGIN) },
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
