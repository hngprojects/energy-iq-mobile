package com.hng14.energyiq.features.alerts.presentation

import androidx.lifecycle.ViewModel
import com.hng14.energyiq.features.alerts.data.AlertRepository
import com.hng14.energyiq.features.alerts.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AlertState(
    val stats: List<AlertStat> = emptyList(),
    val alerts: List<SmartAlertItem> = emptyList(),
    val selectedFilter: AlertFilter = AlertFilter.ALL,
    val inspectedAlertId: String? = null
)

class AlertViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlertState())
    val state: StateFlow<AlertState> = _state.asStateFlow()

    init {
        _state.update { 
            it.copy(
                stats = repository.smartAlertStats,
                alerts = repository.smartAlertItems
            )
        }
    }

    fun onFilterSelected(filter: AlertFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    fun onInspectAlert(alertId: String?) {
        _state.update { it.copy(inspectedAlertId = alertId) }
    }

    fun getDialogContent(alertId: String): SmartAlertDialogContent? {
        return repository.buildSmartAlertDialogContent(alertId)
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
