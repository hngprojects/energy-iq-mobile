package com.hng14.energyiq.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.OnAuthSuccess
import com.hng14.energyiq.features.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AuthViewModel(
    private val repository: AuthRepository,
    initialMode: AuthMode = AuthMode.LOGIN,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState(mode = initialMode))
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _emailVerificationState: MutableStateFlow<EmailVerificationState?> = MutableStateFlow(null)
    val emailVerificationState: StateFlow<EmailVerificationState?> = _emailVerificationState.asStateFlow()


    fun onToggleMode() {
        _state.update { current ->
            AuthState(
                mode = when (current.mode) {
                    AuthMode.LOGIN -> AuthMode.REGISTER
                    AuthMode.REGISTER -> AuthMode.LOGIN
                    AuthMode.FORGOT_PASSWORD -> AuthMode.FORGOT_PASSWORD
                    AuthMode.CHECK_MAIL -> AuthMode.CHECK_MAIL
                    AuthMode.RESET_SUCCESS -> AuthMode.RESET_SUCCESS
                },
            )
        }
    }

    fun onNameChange(value: String) {
        _state.update { it.copy(fullName = value, fullNameError = null, generalError = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _state.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onSubmit(onSuccess: OnAuthSuccess) {

        if (!validateInputs()) return
        val current = _state.value
        val splitName = current.fullName.toBackendNameParts()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            runCatching {
                when (current.mode) {
                    AuthMode.LOGIN -> repository.login(
                        email = current.email.trim(),
                        password = current.password,
                    )

                    AuthMode.REGISTER -> repository.register(
                        firstName = splitName.firstName,
                        lastName = splitName.lastName,
                        email = current.email.trim(),
                        password = current.password,
                    )
                    AuthMode.FORGOT_PASSWORD -> { /* TODO: implement forgot password */ }
                    AuthMode.CHECK_MAIL -> { /* TODO: implement check mail */ }
                    AuthMode.RESET_SUCCESS -> { /* TODO: implement reset success */ }
                }
            }.onSuccess {
                onSuccess(current.mode)
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        generalError = error.message ?: "Something went wrong. Please try again.",
                    )
                }
            }
            _state.update {
                it.copy(isLoading = false, )
            }
        }
    }

    private fun validateInputs(): Boolean {
        val s = _state.value
        val nameError =
            if (s.mode == AuthMode.REGISTER && s.fullName.isBlank()) "Name is required" else null
        val emailError = when {
            s.email.isBlank() -> "Email is required"
            !s.email.contains('@') -> "Enter a valid email address"
            else -> null
        }
        val passwordError = when {
            s.password.isBlank() -> "Password is required"
            s.password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
//        val confirmPasswordError =
//            if (s.mode == AuthMode.REGISTER && s.confirmPassword != s.password) {
//                "Passwords do not match"
//            } else null

        _state.update {
            it.copy(
                fullNameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
               // confirmPasswordError = confirmPasswordError,
            )
        }
        return nameError == null && emailError == null && passwordError == null //&& confirmPasswordError == null
    }

    fun onFullNameChange(value: String) {
        _state.update { it.copy(fullName = value, fullNameError = null, generalError = null) }
    }

    fun resetToMode(mode: AuthMode) {
        _state.update { AuthState(mode = mode) }
    }

    fun onShowForgotPassword() {
        _state.update { it.copy(mode = AuthMode.FORGOT_PASSWORD) }
    }

    fun onForgotPasswordSubmit() {
        // TODO: call backend forgot password API
        _state.update { it.copy(mode = AuthMode.CHECK_MAIL) }
    }

    fun onResetPasswordSubmit() {
        // TODO: call backend reset password API
        _state.update { it.copy(mode = AuthMode.RESET_SUCCESS) }
    }

    fun onBackToLogin() {
        _state.update { AuthState(mode = AuthMode.LOGIN) }
    }

    fun onStartEmailVerification() {
        _emailVerificationState.update { EmailVerificationState.Typing }
    }

    fun onOtpChange(value: String) {
        _state.update { it.copy(otpCode = value, otpError = null) }
        // clear error state when user starts retyping
        if (_emailVerificationState.value == EmailVerificationState.Error) {
            _emailVerificationState.update { EmailVerificationState.Typing }
        }
    }

    fun onVerifyOtp(onSuccess: () -> Unit) {
        val current = _state.value
        viewModelScope.launch {
            _emailVerificationState.update { EmailVerificationState.Verifying }
            runCatching {
                repository.verifyEmail(
                    email = current.email.trim(),
                    otp = current.otpCode,
                )
            }.onSuccess {
                _emailVerificationState.update { EmailVerificationState.Success }
            }.onFailure { error ->
                _emailVerificationState.update { EmailVerificationState.Error }
                _state.update { it.copy(otpError = error.message ?: "Invalid code") }
            }
        }
    }

    fun onBackToSignUp() {
        _emailVerificationState.update { null }
        _state.update { AuthState(mode = AuthMode.REGISTER) }
    }

}

private data class BackendNameParts(
    val firstName: String,
    val lastName: String,
)

private fun String.normalizedNameParts(): List<String> {
    return trim().split(Regex("\\s+")).filter { it.isNotBlank() }
}

private fun startsWithLetter(value: String): Boolean {
    return value.firstOrNull()?.isLetter() == true
}

private fun String.toBackendNameParts(): BackendNameParts {
    val parts = normalizedNameParts()
    val firstName = parts.firstOrNull().orEmpty()
    val lastName = parts.drop(1).joinToString(" ")
    return BackendNameParts(
        firstName = firstName,
        lastName = lastName,
    )
}

