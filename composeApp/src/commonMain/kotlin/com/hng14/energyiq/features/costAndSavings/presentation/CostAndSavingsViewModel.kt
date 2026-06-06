package com.hng14.energyiq.features.costAndSavings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.costAndSavings.data.CostAndSavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

class CostAndSavingsViewModel(
    private val repository: CostAndSavingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CostAndSavingsUiState())
    val uiState: StateFlow<CostAndSavingsUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun onTimeframeSelected(timeframe: String) {
        _uiState.update { it.copy(selectedTimeframe = timeframe) }
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val todayStr = now.date.toString()
                
                val response = repository.getInverterSavings(
                    period = _uiState.value.selectedTimeframe.lowercase(),
                    date = todayStr
                )
                
                if (response.success) {
                    val data = response.data
                    val todayData = data.results.breakdown.lastOrNull()
                    
                    _uiState.update {
                        it.copy(
                            totalSaved = "₦ ${data.results.totalCostSavedNgn}",
                            totalSavedTrend = "",
                            energyConsumed = "${data.summary.totalEnergyConsumedKwh} kWh",
                            energyTrend = "",
                            generationToday = "${todayData?.energyKwh ?: 0.0} kWh",
                            generationRemaining = "",
                            chartData = if (data.chart.isEmpty()) it.chartData else data.chart.map { item -> item.savingsNgn.toFloat() },
                            chartLabels = if (data.chart.isEmpty()) it.chartLabels else data.chart.map { item -> formatLabel(item.label) },
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun formatLabel(isoString: String): String {
        return try {
            val instant = Instant.parse(isoString)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val day = dateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            "$day ${dateTime.day}"
        } catch (_: Exception) {
            isoString.take(10)
        }
    }
}
