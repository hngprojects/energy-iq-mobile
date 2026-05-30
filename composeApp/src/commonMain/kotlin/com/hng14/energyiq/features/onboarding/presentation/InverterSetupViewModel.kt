package com.hng14.energyiq.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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

    fun connectInverter(
        brand: String,
        values: Map<String, String>,
        onSuccess: () -> Unit,
    ) {
        if (_state.value.isConnecting) return
        
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true, connectError = null) }

            runCatching {
                repository.connectInverter(
                    brand = brand.uppercase(),
                    victronAccessToken = values["vrm_api_token"],
                    growattApiToken = values["growatt_api_token"],
                    solarmanEmail = values["solarman_email"],
                    solarmanPassword = values["solarman_password"],
                    sandboxAccessToken = values["sandbox_access_token"],
                )
                // Double check that the ID is actually saved
                var savedId: String? = null
                var retries = 0
                while (savedId == null && retries < 5) {
                    savedId = repository.getSavedInverterId()
                    if (savedId == null) delay(500)
                    retries++
                }
                if (savedId == null) throw Exception("System error: Failed to save inverter configuration.")
                repository.markOnboardingComplete()
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

    fun onConnectErrorDismissed() {
        _state.update { it.copy(connectError = null) }
    }
}
