package com.hng14.energyiq.features.auth.presentation.emailVerification


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hng14.energyiq.features.auth.presentation.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun EmailVerificationScreen(
    onContinue: () -> Unit,
    onBackToSignUp: () -> Unit,
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val authState by viewModel.state.collectAsStateWithLifecycle()
    val verificationState by viewModel.emailVerificationState.collectAsStateWithLifecycle()

    // Set initial state to Typing when screen loads
    LaunchedEffect(Unit) {
        viewModel.onStartEmailVerification()
    }

    val currentState = verificationState ?: return

    EmailVerificationRoute(
        state = currentState,
        firstName = authState.fullName.substringBefore(" ").ifBlank { authState.fullName },
        otpValue = authState.otpCode,
        onOtpChange = viewModel::onOtpChange,
        onVerifyClick = { viewModel.onVerifyOtp(onSuccess = onContinue) },
        onBackToSignUp = {
            viewModel.onBackToSignUp()
            onBackToSignUp()
        },
        onContinue = onContinue,
    )
}