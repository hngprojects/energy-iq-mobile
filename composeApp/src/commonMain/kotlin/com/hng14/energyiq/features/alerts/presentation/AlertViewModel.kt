package com.hng14.energyiq.features.alerts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.alerts.data.AlertRepository
import com.hng14.energyiq.features.alerts.domain.model.*
import com.hng14.energyiq.features.auth.data.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AlertState(
    val stats: List<AlertStat> = emptyList(),
    val alerts: List<SmartAlertItem> = emptyList(),
    val selectedType: AlertType? = AlertType.BATTERY_PERCENTAGE,
    val selectedFilter: AlertFilter = AlertFilter.ALL,
    val inspectedAlertId: String? = null,
    val isLoading: Boolean = false,
    val isResolving: Boolean = false,
    val errorMessage: String? = null,
    val summary: com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryData? = null,
)

class AlertViewModel(
    private val repository: AlertRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AlertState())
    val state: StateFlow<AlertState> = _state.asStateFlow()
    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        loadData()
        startPolling()
    }

    private fun loadData() {
        if (_state.value.isLoading) return
        val currentType = _state.value.selectedType ?: AlertType.BATTERY_PERCENTAGE
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Step 1: Load from manual cache instantly
            launch {
                runCatching {
                    repository.getCachedAlertSummary()
                }.onSuccess { cached ->
                    if (cached != null) {
                        _state.update {
                            it.copy(
                                summary = cached.data,
                                stats = buildStatsFromSummary(cached.data)
                            )
                        }
                    }
                }
            }

            launch {
                runCatching {
                    repository.getCachedAlerts(currentType)
                }.onSuccess { cached ->
                    if (cached != null) {
                        _state.update { it.copy(alerts = cached) }
                    }
                }
            }

            // Step 2: Fetch from network to update
            val alertsDeferred = runCatching {
                repository.fetchAlerts(alertType = currentType)
            }

            val summaryDeferred = runCatching {
                repository.fetchAlertSummary()
            }

            alertsDeferred.onSuccess { alerts ->
                _state.update { it.copy(alerts = alerts, errorMessage = null) }
            }.onFailure { error ->
                if (_state.value.alerts.isEmpty()) {
                    _state.update { it.copy(errorMessage = error.message ?: "Unable to load alerts.") }
                }
            }

            summaryDeferred.onSuccess { response ->
                _state.update {
                    it.copy(
                        summary = response.data,
                        stats = buildStatsFromSummary(response.data),
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                if (_state.value.summary == null) {
                    _state.update { it.copy(errorMessage = error.message ?: "Unable to load summary.") }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                // Safety: Stop polling if user logged out
                if (authRepository.getCurrentUser() == null) {
                    break
                }

                kotlinx.coroutines.delay(45_000) // Poll every 45 seconds for alerts
                val currentType = _state.value.selectedType ?: AlertType.BATTERY_PERCENTAGE
                
                // Refresh summary
                runCatching {
                    repository.fetchAlertSummary()
                }.onSuccess { response ->
                    if (response.data != _state.value.summary) {
                        _state.update {
                            it.copy(
                                summary = response.data,
                                stats = buildStatsFromSummary(response.data),
                                errorMessage = null
                            )
                        }
                    }
                }

                // Refresh alerts list
                runCatching {
                    repository.fetchAlerts(alertType = currentType)
                }.onSuccess { alerts ->
                    if (alerts != _state.value.alerts) {
                        _state.update { it.copy(alerts = alerts, errorMessage = null) }
                    }
                }
            }
        }
    }

    private fun buildStats(alerts: List<SmartAlertItem>): List<AlertStat> {
        if (alerts.isEmpty()) return emptyList()
        val active = alerts.count { !it.resolved }
        val critical = alerts.count { it.severity == AlertSeverity.CRITICAL }
        val warning = alerts.count { it.severity == AlertSeverity.WARNING }
        val unresolved = alerts.count { !it.resolved }
        return listOf(
            AlertStat("Active Alerts", active.toString(), "This page", androidx.compose.ui.graphics.Color(0xFF84CC16)),
            AlertStat("Critical", critical.toString(), "Need action now", androidx.compose.ui.graphics.Color(0xFFEF4444)),
            AlertStat("Warning", warning.toString(), "Awaiting review", androidx.compose.ui.graphics.Color(0xFFF59E0B)),
            AlertStat("Unresolved", unresolved.toString(), "Still open", androidx.compose.ui.graphics.Color(0xFF111827)),
        )
    }

    private fun buildStatsFromSummary(summary: com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryData): List<AlertStat> {
        return listOf(
            AlertStat("Active Alerts", summary.active.toString(), "Currently active", androidx.compose.ui.graphics.Color(0xFF84CC16)),
            AlertStat("Critical", summary.critical.toString(), "Immediate action", androidx.compose.ui.graphics.Color(0xFFEF4444)),
            AlertStat("Warning", summary.warning.toString(), "Needs review", androidx.compose.ui.graphics.Color(0xFFF59E0B)),
            AlertStat("Unresolved", summary.unresolved.toString(), "Still open", androidx.compose.ui.graphics.Color(0xFF111827)),
        )
    }

    fun onFilterSelected(filter: AlertFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    fun onTypeSelected(type: AlertType?) {
        _state.update { it.copy(selectedType = type) }
        loadData()
    }

    fun onInspectAlert(alertId: String?) {
        _state.update { it.copy(inspectedAlertId = alertId) }
    }

    fun getDialogContent(alertId: String): SmartAlertDialogContent? {
        val alert = _state.value.alerts.firstOrNull { it.id == alertId }
            ?: repository.smartAlertItems.firstOrNull { it.id == alertId }
            ?: return null
        return repository.buildSmartAlertDialogContent(alert)
    }

    fun onErrorDismissed() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        loadData()
    }

    fun onResolveAlert(alertId: String) {
        if (_state.value.isResolving) return
        viewModelScope.launch {
            _state.update { it.copy(isResolving = true, errorMessage = null) }
            runCatching {
                repository.resolveAlert(alertId)
            }.onSuccess {
                _state.update { it.copy(isResolving = false, inspectedAlertId = null) }
                loadData() // Refresh to show updated status
            }.onFailure { error ->
                _state.update { 
                    it.copy(
                        isResolving = false, 
                        errorMessage = error.message ?: "Failed to resolve alert." 
                    ) 
                }
            }
        }
    }

    val visibleAlerts: List<SmartAlertItem>
        get() {
            val alerts = _state.value.alerts
            val filter = _state.value.selectedFilter
            return alerts.filter { alert ->
                when (filter) {
                    AlertFilter.SUCCESS -> alert.severity == AlertSeverity.SUCCESS
                    AlertFilter.WARNING -> alert.severity == AlertSeverity.WARNING
                    AlertFilter.CRITICAL -> alert.severity == AlertSeverity.CRITICAL
                    AlertFilter.RESOLVED -> alert.resolved
                    AlertFilter.UNRESOLVED -> !alert.resolved
                    AlertFilter.ALL -> true
                }
            }
        }
}
