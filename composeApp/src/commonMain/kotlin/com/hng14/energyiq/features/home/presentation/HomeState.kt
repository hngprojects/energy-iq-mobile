package com.hng14.energyiq.features.home.presentation

import com.hng14.energyiq.features.auth.domain.model.User

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
)
