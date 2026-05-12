package com.hng14.energyiq.features.auth.presentation

import com.hng14.energyiq.features.auth.AuthMode

data class AuthState(
    val mode: AuthMode = AuthMode.LOGIN,
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,

    val otpCode: String = "",
    val otpError: String? = null,
    val isVerifyingOtp: Boolean = false,
)

sealed interface EmailVerificationState {
    data object Typing : EmailVerificationState
    data object Verifying : EmailVerificationState
    data object Error : EmailVerificationState
    data object Success : EmailVerificationState
}




