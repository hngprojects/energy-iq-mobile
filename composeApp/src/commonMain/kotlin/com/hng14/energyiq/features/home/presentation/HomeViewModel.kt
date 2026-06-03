package com.hng14.energyiq.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.home.OnLogout
import com.hng14.energyiq.features.home.data.HealthLogRepository
import com.hng14.energyiq.features.home.data.HomeRepository
import com.hng14.energyiq.features.home.data.remote.dto.InverterDashboardData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val healthLogRepository: HealthLogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    private var pollingJob: Job? = null

    init {
        loadData()
        startPolling()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // User profile is already cached on login/app-start
            val cachedUser = repository.getCurrentUser()
            if (cachedUser != null) {
                _state.update { it.copy(user = cachedUser) }
            }

            // Load from manual dashboard cache instantly
            runCatching {
                homeRepository.getCachedInverterDashboard()
            }.onSuccess { cached ->
                if (cached?.data?.currentReadings != null) {
                    _state.update { it.copy(dashboardData = cached.data) }
                }
            }

            // Fetch from network with extended retry for first-time recovery
            var attempt = 0
            val maxAttempts = 5
            var success = false
            var lastError: Throwable? = null

            while (attempt < maxAttempts && !success) {
                runCatching {
                    homeRepository.getInverterDashboard()
                }.onSuccess { response ->
                    val data = response?.response?.data
                    if (data?.currentReadings != null) {
                        _state.update { it.copy(dashboardData = data, errorMessage = null) }
                        if (response != null) {
                            healthLogRepository.recordFromDashboard(
                                userId = response.userId,
                                inverterId = response.inverterId,
                                data = data,
                            )
                        }
                        success = true
                    } else if (_state.value.dashboardData != null) {
                        success = true
                    }
                }.onFailure { e ->
                    lastError = e
                }

                if (!success) {
                    delay(3000) // Wait 3 seconds before next retry
                }
                attempt++
            }

            // Final fallback check
            if (_state.value.dashboardData == null) {
                runCatching { homeRepository.getInverterDashboard() }.onSuccess { response ->
                    val data = response?.response?.data
                    if (data != null) {
                        _state.update { it.copy(dashboardData = data, errorMessage = null) }
                        if (response != null) {
                            healthLogRepository.recordFromDashboard(
                                userId = response.userId,
                                inverterId = response.inverterId,
                                data = data,
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                errorMessage = lastError?.message
                                    ?: "Connecting to inverter... Pull to refresh in a moment.",
                            )
                        }
                    }
                }.onFailure { e ->
                    _state.update {
                        it.copy(
                            errorMessage = e.message
                                ?: "Unable to load dashboard. Pull to refresh and try again.",
                        )
                    }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                // Extra safety: Stop polling if we are logging out or lost session
                if (_state.value.isLoggingOut || repository.getCurrentUser() == null) {
                    break
                }

                // If we don't have dashboard data yet, try immediately
                // Otherwise, wait 30 seconds
                if (_state.value.dashboardData != null) {
                    delay(30_000)
                }

                runCatching {
                    homeRepository.getInverterDashboard()
                }.onSuccess { response ->
                    val newData = response?.response?.data
                    if (newData != null && newData != _state.value.dashboardData) {
                        _state.update { it.copy(dashboardData = newData, errorMessage = null) }
                        if (response != null) {
                            healthLogRepository.recordFromDashboard(
                                userId = response.userId,
                                inverterId = response.inverterId,
                                data = newData,
                            )
                        }
                    }
                }
                
                // Safety delay to prevent infinite fast loops if fetch fails
                if (_state.value.dashboardData == null) {
                    delay(5000)
                }
            }
        }
    }

    fun onLogout(onLogout: OnLogout) {
        if (_state.value.isLoggingOut) return
        
        // Stop polling immediately
        pollingJob?.cancel()
        pollingJob = null

        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true, errorMessage = null) }
            runCatching {
                repository.logout()
            }.onSuccess {
                onLogout()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.message ?: "Unable to sign out. Please try again.",
                    )
                }
            }
            _state.update { it.copy(isLoggingOut = false) }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onDismissHealthBanner() {
        _state.update { it.copy(isHealthBannerDismissed = true) }
    }
}
