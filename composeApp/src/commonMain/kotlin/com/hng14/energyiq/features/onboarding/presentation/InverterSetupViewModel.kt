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
}
