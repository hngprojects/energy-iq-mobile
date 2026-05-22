package com.hng14.energyiq.features.alerts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.alerts.data.AlertRepository
import com.hng14.energyiq.features.alerts.domain.model.*
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
    val errorMessage: String? = null,
)

class AlertViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlertState())
    val state: StateFlow<AlertState> = _state.asStateFlow()

    init {
        loadAlerts()
    }

    fun onFilterSelected(filter: AlertFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    fun onTypeSelected(type: AlertType?) {
        _state.update { it.copy(selectedType = type) }
        loadAlerts()
    }

    fun onInspectAlert(alertId: String?) {
        _state.update { it.copy(inspectedAlertId = alertId) }
    }

    fun getDialogContent(alertId: String): SmartAlertDialogContent? {
        return repository.buildSmartAlertDialogContent(alertId)
    }

    fun onErrorDismissed() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun loadAlerts(pageNumber: Int = 1, pageSize: Int = 10) {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Temporary: Use dummy data until API issue is resolved
            kotlinx.coroutines.delay(500) // Simulate network delay
            val dummyAlerts = repository.smartAlertItems
            
            _state.update {
                it.copy(
                    alerts = dummyAlerts,
                    stats = buildStats(dummyAlerts),
                    isLoading = false,
                    errorMessage = null,
                )
            }
            
            /* API Integration (Commented out for now)
            runCatching {
                repository.fetchAlerts(
                    alertType = _state.value.selectedType,
                    pageNumber = pageNumber,
                    pageSize = pageSize,
                )
            }.onSuccess { alerts ->
                _state.update {
                    it.copy(
                        alerts = alerts,
                        stats = buildStats(alerts),
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load alerts.",
                    )
                }
            }
            */
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
