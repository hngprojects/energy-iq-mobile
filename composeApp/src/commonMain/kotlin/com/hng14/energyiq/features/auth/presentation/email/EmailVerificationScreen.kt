package com.hng14.energyiq.features.auth.presentation.email

import androidx.compose.runtime.Composable

import com.hng14.energyiq.features.auth.presentation.EmailVerificationState

@Composable
fun EmailVerificationScreen(
    onAction: () -> Unit = {},
){

    //val currentState = emailVerificationState ?: return //if no state is set yet. safety net
    // State 1
    val currentState: EmailVerificationState = EmailVerificationState.ConfirmEmailAccount

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
                //name = authState.name.ifBlank { "Amaka" } ,
                otp = "435724",
                onAction = onAction
            )
        }
    )
}