package com.hng14.energyiq.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.home.OnLogout
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
            
            val userJob = launch {
                runCatching {
                    repository.getMe()
                }.onSuccess { user ->
                    _state.update { it.copy(user = user) }
                }.onFailure {
                    val user = repository.getCurrentUser()
                    _state.update { it.copy(user = user) }
                }
            }

            val dashboardJob = launch {
                // Step 1: Load from manual cache instantly
                runCatching {
                    homeRepository.getCachedInverterDashboard()
                }.onSuccess { cached ->
                    if (cached?.data?.currentReadings != null) {
                        _state.update { it.copy(dashboardData = cached.data) }
                    }
                }

                // If no cache, mark as initial sync
                if (_state.value.dashboardData == null) {
                    _state.update { it.copy(isInitialSync = true) }
                }

                // Step 2: Fetch from network with extended retry for first-time setup
                var attempt = 0
                val maxAttempts = 3
                var success = false
                
                while (attempt < maxAttempts && !success) {
                    runCatching {
                        homeRepository.getInverterDashboard()
                    }.onSuccess { response ->
                        val data = response?.data
                        if (data?.currentReadings != null) {
                            _state.update { it.copy(dashboardData = data, isInitialSync = false, errorMessage = null) }
                            success = true
                        } else if (_state.value.dashboardData != null) {
                            _state.update { it.copy(isInitialSync = false) }
                            success = true
                        }
                    }
                    
                    if (!success) {
                        delay(2000)
                    }
                    attempt++
                }
                
                if (_state.value.dashboardData == null && !success) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isInitialSync = true,
                            errorMessage = "We're setting up your dashboard. This usually takes a few moments as we connect to your inverter."
                        ) 
                    }
                } else {
                    _state.update { it.copy(isInitialSync = false) }
                }
            }

            userJob.join()
            dashboardJob.join()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                // If we don't have dashboard data yet, try immediately
                // Otherwise, wait 30 seconds
                if (_state.value.dashboardData != null) {
                    delay(30_000)
                }

                runCatching {
                    homeRepository.getInverterDashboard()
                }.onSuccess { response ->
                    val newData = response?.data
                    if (newData != null && newData != _state.value.dashboardData) {
                        _state.update { it.copy(dashboardData = newData, errorMessage = null) }
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
