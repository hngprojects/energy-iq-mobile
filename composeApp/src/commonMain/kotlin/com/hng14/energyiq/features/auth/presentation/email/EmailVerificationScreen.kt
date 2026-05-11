package com.hng14.energyiq.features.auth.presentation.email

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hng14.energyiq.features.auth.presentation.AuthViewModel

import com.hng14.energyiq.features.auth.presentation.EmailVerificationState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationScreen(
    onAction: () -> Unit = {},
){
    val viewModel = koinViewModel<AuthViewModel>()
    val emailVerificationState by viewModel.emailVerificationState.collectAsStateWithLifecycle()
    val currentState = emailVerificationState ?: return
    //val currentState = emailVerificationState ?: return //if no state is set yet. safety net
    // State 1
    //val currentState: EmailVerificationState = EmailVerificationState.ConfirmEmailAccount

// State 2
    //val currentState: EmailVerificationState = EmailVerificationState.ResetPassword

// State 3
    //val currentState: EmailVerificationState = EmailVerificationState.VerificationLinkExpired

// State 4
   // val currentState: EmailVerificationState = EmailVerificationState.UpdatedPassword

// State 5
    //val currentState: EmailVerificationState = EmailVerificationState.IsVerificationSuccess

    FixedContent(
        dynamicContent = {
            EmailVerificationRoute(
                state = currentState,
                name = "Amaka",
                otp = "435724",
                onAction = onAction
            )
        }
    )
}