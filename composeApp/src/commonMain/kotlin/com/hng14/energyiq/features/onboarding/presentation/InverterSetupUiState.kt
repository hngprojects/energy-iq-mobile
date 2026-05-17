package com.hng14.energyiq.features.onboarding.presentation

data class InverterSetupUiState(
    val supportedBrands: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
