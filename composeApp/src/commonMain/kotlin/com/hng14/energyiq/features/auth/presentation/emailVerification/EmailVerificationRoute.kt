package com.hng14.energyiq.features.auth.presentation.emailVerification

import androidx.compose.runtime.Composable
import com.hng14.energyiq.features.auth.presentation.EmailVerificationState


@Composable
fun EmailVerificationRoute(
    state: EmailVerificationState,
    firstName: String,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onBackToSignUp: () -> Unit,
    onContinue: () -> Unit,
) {
    when (state) {
        EmailVerificationState.Typing -> EmailVerificationContent(
            firstName = firstName,
            otpValue = otpValue,
            onOtpChange = onOtpChange,
            isError = false,
            isVerifying = false,
            onVerifyClick = onVerifyClick,
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Verifying -> EmailVerificationContent(
            firstName = firstName,
            otpValue = otpValue,
            onOtpChange = onOtpChange,
            isError = false,
            isVerifying = true,
            onVerifyClick = onVerifyClick,
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Error -> EmailVerificationContent(
            firstName = firstName,
            otpValue = otpValue,
            onOtpChange = onOtpChange,
            isError = true,
            isVerifying = false,
            onVerifyClick = onVerifyClick,
            onBackToSignUp = onBackToSignUp,
        )

        EmailVerificationState.Success -> EmailVerificationSuccessContent(
            onContinue = onContinue,
        )
    }
}