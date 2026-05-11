package com.hng14.energyiq.features.auth.presentation.email

import androidx.compose.runtime.Composable
import com.hng14.energyiq.features.auth.presentation.EmailVerificationState


@Composable
fun EmailVerificationRoute(
    state: EmailVerificationState,
    name: String = "Amaka",
    otp: String = "435714",
    onAction: () -> Unit = {},

    ){

   // FixedContent(
        //dynamicContent = {
            when(state){
                EmailVerificationState.ConfirmEmailAccount -> {
                    ConfirmEmailAccountContent(
                        name = name,
                        otp = otp,
                    )
                }

                EmailVerificationState.IsVerificationSuccess -> {
                    IsVerificationSuccessContent(
                        name = name,
                        onLoginClick = onAction,
                    )
                }

                EmailVerificationState.ResetPassword -> {
                    ResetPasswordContent(
                        name = name,
                        onResetClick = onAction,
                    )
                }

                EmailVerificationState.UpdatedPassword -> {
                    UpdatedPasswordContent(
                        name = name,
                        onLoginClick = onAction,
                    )
                }

                EmailVerificationState.VerificationLinkExpired -> {
                    VerificationLinkExpiredContent(
                        name = name,
                        onResendClick = onAction,
                    )
                }
            }
        }
    //)
//}
