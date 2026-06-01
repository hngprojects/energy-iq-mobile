package com.hng14.energyiq.features.home.presentation

import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.features.home.data.remote.dto.InverterDashboardData

data class HomeState(
    val user: User? = null,
    val dashboardData: InverterDashboardData? = null,
    val isLoading: Boolean = true,
    val isInitialSync: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isHealthBannerDismissed: Boolean = false,
    val errorMessage: String? = null,
)
