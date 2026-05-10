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

    fun onToggleMode() {
        _state.update { current ->
            AuthState(
                mode = when (current.mode) {
                    AuthMode.LOGIN -> AuthMode.REGISTER
                    AuthMode.REGISTER -> AuthMode.LOGIN
                },
            )
        }
    }
    fun onFirstNameChange(value: String) {
        _state.update { it.copy(firstName = value, firstNameError = null, generalError = null) }
    }
    fun onLastNameChange(value: String) {
        _state.update { it.copy(lastName = value, lastNameError = null, generalError = null) }
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
        if (_state.value.isLoading) return
        if (!validateInputs()) return
        val current = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            runCatching {
                when (current.mode) {
                    AuthMode.LOGIN -> repository.login(
                        email = current.email.trim(),
                        password = current.password,
                    )

                    AuthMode.REGISTER -> repository.register(
                        firstName = current.firstName.trim(),
                        lastName = current.lastName.trim(),
                        email = current.email.trim(),
                        password = current.password,
                    )
                }
            }.onSuccess {
                onSuccess()
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
        val firstNameError =
            if (s.mode == AuthMode.REGISTER && s.firstName.isBlank()) "First Name is required" else null
        val lastNameError =
            if (s.mode == AuthMode.REGISTER && s.lastName.isBlank()) "Last Name is required" else null
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
        val confirmPasswordError =
            if (s.mode == AuthMode.REGISTER && s.confirmPassword != s.password) {
                "Passwords do not match"
            } else null

        _state.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,

            )
        }
        return firstNameError == null && lastNameError == null  && emailError == null && passwordError == null && confirmPasswordError == null
    }
}
