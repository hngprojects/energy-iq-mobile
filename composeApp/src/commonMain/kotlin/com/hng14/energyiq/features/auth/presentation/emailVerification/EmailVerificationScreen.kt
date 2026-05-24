package com.hng14.energyiq.features.auth.presentation.emailVerification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hng14.energyiq.core.ui.ServerErrorDialog
import com.hng14.energyiq.features.auth.presentation.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationScreen(
    fullName: String,
    email: String,
    onContinue: () -> Unit,
    onBackToSignUp: () -> Unit,
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(fullName, email) {
        viewModel.onStartEmailVerification(
            fullName = fullName,
            email = email,
        )
    }

    state.generalError?.let { message ->
        ServerErrorDialog(
            message = message,
            onDismiss = viewModel::onDismissGeneralError,
        )
    }

    if (state.emailVerificationState == EmailVerificationState.Success) {
        EmailVerificationSuccessContent(
            firstName = fullName.substringBefore(" ").ifBlank { fullName },
            onContinue = onContinue,
        )
        return
    }

    // Keep the same composable instance across Typing/Error/Verifying so the OTP field retains focus.
    EmailVerificationContent(
        firstName = fullName.substringBefore(" ").ifBlank { fullName },
        otpValue = state.otpCode,
        onOtpChange = viewModel::onOtpChange,
        isError = state.emailVerificationState == EmailVerificationState.Error,
        isVerifying = state.emailVerificationState == EmailVerificationState.Verifying,
        errorMessage = if (state.emailVerificationState == EmailVerificationState.Error) state.generalError else null,
        onVerifyClick = if (state.emailVerificationState == EmailVerificationState.Verifying) {
            {}
        } else {
            viewModel::onVerifyEmailSubmit
        },
        onBackToSignUp = onBackToSignUp,
    )
}
