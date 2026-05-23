package com.hng14.energyiq.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.OnAuthSuccess
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.presentation.emailVerification.EmailVerificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class AuthViewModel(
    private val repository: AuthRepository,
    initialMode: AuthMode = AuthMode.LOGIN,
    initialResetToken: String? = null,
) : ViewModel() {
    private val fullNameRuleMessage = "Enter your first and last name, for example John Doe"
    private val passwordRuleMessage = "Password must be at least 8 characters and a special key"

    private val _state = MutableStateFlow(
        AuthState(
            mode = initialMode,
            resetToken = initialResetToken,
        ),
    )
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        if (initialMode == AuthMode.CHECK_MAIL && initialResetToken?.isNotBlank() == true) {
            viewModelScope.launch {
                val savedEmail = repository.getPendingResetEmail()?.trim().orEmpty()
                if (savedEmail.isNotBlank()) {
                    _state.update {
                        it.copy(
                            email = savedEmail,
                            isResetEmailLocked = true,
                            emailError = null,
                        )
                    }
                }
            }
        }
    }

    fun resetToMode(mode: AuthMode, resetToken: String? = null) {
        _state.update { current ->
            if (current.mode == mode && current.fullName.isEmpty() && current.email.isEmpty() &&
                current.password.isEmpty() && current.confirmPassword.isEmpty() &&
                current.otpCode.isEmpty() &&
                current.fullNameError == null && current.emailError == null &&
                current.passwordError == null && current.confirmPasswordError == null &&
                current.resetToken == resetToken &&
                current.emailVerificationState == EmailVerificationState.Typing &&
                current.generalError == null && !current.isLoading
            ) {
                current
            } else {
                AuthState(mode = mode, resetToken = resetToken)
            }
        }
    }

    fun onToggleMode() {
        _state.update { current ->
            AuthState(
                mode = when (current.mode) {
                    AuthMode.LOGIN -> AuthMode.REGISTER
                    AuthMode.REGISTER -> AuthMode.LOGIN
                    AuthMode.FORGOT_PASSWORD -> AuthMode.LOGIN
                    AuthMode.EMAIL_SENT -> AuthMode.LOGIN
                    AuthMode.CHECK_MAIL -> AuthMode.LOGIN
                    AuthMode.RESET_SUCCESS -> AuthMode.LOGIN
                },
            )
        }
    }

    fun onShowForgotPassword() {
        _state.update {
            AuthState(
                mode = AuthMode.FORGOT_PASSWORD,
                email = it.email,
            )
        }
    }

    fun onBackToLogin() {
        _state.update {
            AuthState(
                mode = AuthMode.LOGIN,
                email = it.email,
                resetToken = it.resetToken,
            )
        }
    }

    fun onForgotPasswordSubmit() {
        if (_state.value.isLoading) return
        val emailError = validateEmail(_state.value.email.trim())
        if (emailError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    generalError = null,
                )
            }
            return
        }
        val email = _state.value.email.trim()
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    emailError = null,
                    generalError = null,
                )
            }
            runCatching {
                withTimeout(120_000) {
                    repository.forgotPassword(email = email)
                }
            }.onSuccess { response ->
                repository.savePendingResetEmail(response.data.email)
                val resetToken = _state.value.resetToken
                _state.value = AuthState(
                    mode = AuthMode.EMAIL_SENT,
                    email = response.data.email,
                    resetToken = resetToken,
                    snackbarMessage = response.message,
                )
            }.onFailure { error ->
                val message = when (error) {
                    is TimeoutCancellationException -> "Request is taking too long. Please check your internet and try again."
                    else -> error.message ?: "Something went wrong. Please try again."
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = message,
                    )
                }
            }
        }
    }

    fun onResendForgotPasswordLink() {
        if (_state.value.isLoading) return
        val email = _state.value.email.trim()
        val emailError = validateEmail(email)
        if (emailError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    generalError = null,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    generalError = null,
                )
            }
            runCatching {
                withTimeout(120_000) {
                    repository.forgotPassword(email = email)
                }
            }.onSuccess { response ->
                repository.savePendingResetEmail(response.data.email)
                _state.update {
                    it.copy(
                        email = response.data.email,
                        isLoading = false,
                        generalError = null,
                        snackbarMessage = response.message,
                    )
                }
            }.onFailure { error ->
                val message = when (error) {
                    is TimeoutCancellationException -> "Request is taking too long. Please check your internet and try again."
                    else -> error.message ?: "Something went wrong. Please try again."
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = message,
                    )
                }
            }
        }
    }

    fun onSnackbarShown() {
        _state.update { it.copy(snackbarMessage = null) }
    }

    fun onDismissGeneralError() {
        _state.update { it.copy(generalError = null) }
    }

    fun onFullNameChange(value: String) {
        _state.update { it.copy(fullName = value, fullNameError = null, generalError = null) }
    }

    fun onEmailChange(value: String) {
        if (_state.value.mode == AuthMode.CHECK_MAIL && _state.value.isResetEmailLocked) return
        _state.update { it.copy(email = value, emailError = null, generalError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _state.update { it.copy(confirmPassword = value, confirmPasswordError = null, generalError = null) }
    }

    fun onRememberMeChange(value: Boolean) {
        _state.update { it.copy(rememberMe = value) }
    }

    fun onStartEmailVerification(fullName: String, email: String) {
        val normalizedEmail = email.trim()
        _state.update { current ->
            if (current.fullName == fullName &&
                current.email == normalizedEmail &&
                current.otpCode.isEmpty() &&
                current.emailVerificationState == EmailVerificationState.Typing
            ) {
                current
            } else {
                current.copy(
                    fullName = fullName,
                    email = normalizedEmail,
                    otpCode = "",
                    emailVerificationState = EmailVerificationState.Typing,
                    generalError = null,
                    isLoading = false,
                )
            }
        }
    }

    fun onOtpChange(value: String) {
        val filtered = value.filter(Char::isDigit).take(6)
        _state.update {
            it.copy(
                otpCode = filtered,
                emailVerificationState = if (it.emailVerificationState == EmailVerificationState.Error) {
                    EmailVerificationState.Typing
                } else {
                    it.emailVerificationState
                },
                generalError = null,
            )
        }
    }

    fun onOtpReset() {
        _state.update { it.copy(otpCode = "") }
    }

    fun onVerifyEmailSubmit() {
        val current = _state.value
        if (current.isLoading || current.otpCode.length != 6) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    generalError = null,
                    emailVerificationState = EmailVerificationState.Verifying,
                )
            }

            runCatching {
                withTimeout(120_000) {
                    repository.verifyEmail(
                        email = current.email.trim(),
                        otp = current.otpCode,
                    )
                }
            }.onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        emailVerificationState = EmailVerificationState.Success,
                    )
                }
            }.onFailure { error ->
                val message = when (error) {
                    is TimeoutCancellationException -> "Verification is taking too long. Please check your internet and try again."
                    else -> error.message ?: "Something went wrong. Please try again."
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = message,
                        emailVerificationState = EmailVerificationState.Error,
                    )
                }
            }
        }
    }

    fun onResetPasswordSubmit() {
        if (_state.value.isLoading) return
        val emailError = validateEmail(_state.value.email.trim())
        val passwordError = when {
            _state.value.password.isBlank() -> "Password is required"
            !isPasswordValid(_state.value.password) -> passwordRuleMessage
            else -> null
        }
        val confirmPasswordError = when {
            _state.value.confirmPassword.isBlank() -> "Confirm password is required"
            _state.value.confirmPassword != _state.value.password -> "Passwords do not match"
            else -> null
        }

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    generalError = null,
                )
            }
            return
        }

        val current = _state.value
        val token = current.resetToken?.trim().orEmpty()
        if (token.isBlank()) {
            _state.update {
                it.copy(generalError = "Reset token is missing. Open the reset link from your email and try again.")
            }
            return
        }

        val email = current.email.trim()
        val password = current.password

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    generalError = null,
                )
            }

            runCatching {
                repository.resetPassword(
                    email = email,
                    password = password,
                    token = token,
                )
            }.onSuccess {
                repository.clearPendingResetEmail()
                _state.value = AuthState(
                    mode = AuthMode.RESET_SUCCESS,
                    email = email,
                )
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = error.message ?: "Something went wrong. Please try again.",
                    )
                }
            }
        }
    }

    fun onSubmit(onSuccess: OnAuthSuccess) {
        if (_state.value.isLoading) return
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
                        isRememberMe = current.rememberMe,
                    )

                    AuthMode.REGISTER -> repository.register(
                        firstName = splitName.firstName,
                        lastName = splitName.lastName,
                        email = current.email.trim(),
                        password = current.password,
                    )

                    AuthMode.FORGOT_PASSWORD, AuthMode.EMAIL_SENT, AuthMode.CHECK_MAIL, AuthMode.RESET_SUCCESS -> Unit
                }
            }.onSuccess {
                _state.value = AuthState(mode = current.mode)
                onSuccess(current.mode, current.fullName, current.email.trim())
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        generalError = error.message ?: "Something went wrong. Please try again.",
                    )
                }
            }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun validateInputs(): Boolean {
        val s = _state.value
        val fullNameError = validateFullName(s)
        val emailError = validateEmail(s.email)
        val passwordError = when (s.mode) {
            AuthMode.LOGIN, AuthMode.REGISTER -> when {
                s.password.isBlank() -> "Password is required"
                !isPasswordValid(s.password) -> passwordRuleMessage
                else -> null
            }

            AuthMode.FORGOT_PASSWORD, AuthMode.EMAIL_SENT, AuthMode.CHECK_MAIL, AuthMode.RESET_SUCCESS -> null
        }

        _state.update {
            it.copy(
                fullNameError = fullNameError,
                emailError = emailError,
                passwordError = passwordError,
            )
        }
        return fullNameError == null && emailError == null && passwordError == null
    }

    private fun validateEmail(email: String): String? {
        val normalizedEmail = email.trim()
        return when {
            normalizedEmail.isBlank() -> "Email is required"
            !EMAIL_REGEX.matches(normalizedEmail) -> "Enter a valid email address"
            else -> null
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private fun validateFullName(state: AuthState): String? {
        if (state.mode != AuthMode.REGISTER) return null

        val normalized = state.fullName.normalizedNameParts()
        return when {
            normalized.isEmpty() -> "Full name is required"
            normalized.size < 2 -> fullNameRuleMessage
            !startsWithLetter(normalized.first()) -> "First name must start with a letter"
            !startsWithLetter(normalized.drop(1).joinToString(" ")) -> "Last name must start with a letter"
            normalized.first().length < 2 -> "First name is too short"
            normalized.drop(1).joinToString(" ").length < 2 -> "Last name is too short"
            else -> null
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasSpecialCharacter = password.any { !it.isLetterOrDigit() }
        return hasMinLength && hasSpecialCharacter
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
}
