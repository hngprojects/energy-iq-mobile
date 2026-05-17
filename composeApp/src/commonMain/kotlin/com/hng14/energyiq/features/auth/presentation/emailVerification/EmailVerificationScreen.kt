package com.hng14.energyiq.features.auth.presentation.emailVerification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    when (state.emailVerificationState) {
        EmailVerificationState.Typing -> EmailVerificationContent(
            firstName = fullName.substringBefore(" ").ifBlank { fullName },
            otpValue = state.otpCode,
            onOtpChange = viewModel::onOtpChange,
            isError = false,
            isVerifying = false,
            onVerifyClick = viewModel::onVerifyEmailSubmit,
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Verifying -> EmailVerificationContent(
            firstName = fullName.substringBefore(" ").ifBlank { fullName },
            otpValue = state.otpCode,
            onOtpChange = viewModel::onOtpChange,
            isError = false,
            isVerifying = true,
            onVerifyClick = {},
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Error -> EmailVerificationContent(
            firstName = fullName.substringBefore(" ").ifBlank { fullName },
            otpValue = state.otpCode,
            onOtpChange = viewModel::onOtpChange,
            isError = true,
            isVerifying = false,
            onVerifyClick = viewModel::onVerifyEmailSubmit,
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Success -> EmailVerificationSuccessContent(
            firstName = fullName.substringBefore(" ").ifBlank { fullName },
            onContinue = onContinue,
        )
    }
}
