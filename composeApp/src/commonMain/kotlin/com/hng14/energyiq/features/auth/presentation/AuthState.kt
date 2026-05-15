package com.hng14.energyiq.features.auth.presentation

import com.hng14.energyiq.features.auth.AuthMode

data class AuthState(
    val mode: AuthMode = AuthMode.LOGIN,
    val fullName: String = "",
    val email: String = "",
    val isResetEmailLocked: Boolean = false,
    val password: String = "",
    val confirmPassword: String = "",
    val resetToken: String? = null,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val snackbarMessage: String? = null,
)
