package com.hng14.energyiq.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InverterSetupViewModel(
    private val repository: OnboardingRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(InverterSetupUiState())
    val state: StateFlow<InverterSetupUiState> = _state.asStateFlow()

    init {
        loadSupportedBrands()
    }

    fun loadSupportedBrands() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching {
                repository.fetchSupportedBrands()
            }.onSuccess { brands ->
                _state.update {
                    it.copy(
                        supportedBrands = brands,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        supportedBrands = emptyList(),
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load supported inverters.",
                    )
                }
            }
        }
    }

    fun connectVictron(
        victronAccessToken: String,
        onSuccess: () -> Unit,
    ) {
        if (_state.value.isConnecting) return
        val token = victronAccessToken.trim()
        if (token.isBlank()) {
            _state.update { it.copy(connectError = "Victron access token is required.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, connectError = null) }

            runCatching {
                repository.connectVictron(victronAccessToken = token)
            }.onSuccess {
                _state.update { it.copy(isConnecting = false, connectError = null) }
                onSuccess()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isConnecting = false,
                        connectError = error.message ?: "Unable to connect inverter.",
                    )
                }
            }
        }
    }

    fun connectSandbox(
        sandboxAccessToken: String,
        onSuccess: () -> Unit,
    ) {
        if (_state.value.isConnecting) return
        val token = sandboxAccessToken.trim()
        if (token.isBlank()) {
            _state.update { it.copy(connectError = "Sandbox access token is required.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, connectError = null) }

            runCatching {
                repository.connectSandbox(sandboxAccessToken = token)
            }.onSuccess {
                _state.update { it.copy(isConnecting = false, connectError = null) }
                onSuccess()
            }.onFailure {
                // Temporarily allow user to pass through even on failure
                _state.update {
                    it.copy(
                        isConnecting = false,
                        connectError = null, // Clear error to hide dialog
                    )
                }
                onSuccess() // Still navigate to next screen
            }
        }
    }

    fun onConnectErrorDismissed() {
        _state.update { it.copy(connectError = null) }
    }
}
