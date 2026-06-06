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

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
                coroutineScope {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val todayStr = now.date.toString()

                    val savingsJob = async {
                        repository.getInverterSavings(
                            period = _uiState.value.selectedTimeframe.lowercase(),
                            date = todayStr
                        )
                    }

                    val cumulativeJob = async {
                        repository.getCumulativeSavings()
                    }

                    val savingsResponse = try { savingsJob.await() } catch (e: Exception) { null }
                    val cumulativeResponse = try { cumulativeJob.await() } catch (e: Exception) { null }

                    if (savingsResponse?.success == true) {
                        val data = savingsResponse.data
                        val todayData = data.results.breakdown.lastOrNull()

                        _uiState.update {
                            it.copy(
                                totalSaved = "₦ ${formatCurrency(data.results.totalCostSavedNgn)}",
                                totalSavedTrend = "",
                                energyConsumed = "${data.summary.totalEnergyConsumedKwh} kWh",
                                energyTrend = "",
                                generationToday = "${todayData?.energyKwh ?: 0.0} kWh",
                                generationRemaining = "",
                                chartData = if (data.chart.isEmpty()) it.chartData else data.chart.map { item -> item.savingsNgn.toFloat() },
                                chartLabels = if (data.chart.isEmpty()) it.chartLabels else data.chart.map { item -> formatLabel(item.label) },
                            )
                        }
                    }

                    if (cumulativeResponse?.success == true) {
                        val cData = cumulativeResponse.data
                        _uiState.update {
                            it.copy(
                                lifetimeSavings = "₦ ${formatLargeNumber(cData.lifetimeSavingsNgn)}",
                                lifetimeSavingsTrend = "+12%", // Still mocking trend
                                co2Avoided = "${((cData.co2AvoidedKg / 1000.0) * 10).toInt() / 10.0} Tons",
                                genHoursAvoided = "${cData.generatorHoursAvoided.toInt()} hrs",
                                fuelSaved = "${cData.lifetimeFuelSavedLitres.toInt()} Litres",
                                cumulativeActualSavings = if (cData.chart.isEmpty()) it.cumulativeActualSavings else cData.chart.map { item -> item.savingsNgn.toFloat() },
                                cumulativeGridProjection = if (cData.chart.isEmpty()) it.cumulativeGridProjection else cData.chart.map { item -> (item.savingsNgn * 0.6).toFloat() }, // Mock projection
                                cumulativeChartLabels = if (cData.chart.isEmpty()) it.cumulativeChartLabels else cData.chart.map { item -> formatChartLabel(item.month) },
                                totalSavingsToDate = "₦ ${formatCurrency(cData.totalSavingsToDateNgn)}",
                                totalSavingsTrend = "+14.2%",
                                avgMonthlySavings = "₦ ${formatCurrency(cData.averageMonthlySavingsNgn)}",
                                avgMonthlySavingsTrend = "+2.4%",
                            )
                        }
                    }
                }
                _uiState.update { it.copy(isLoading = false) }
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

    private fun formatChartLabel(monthString: String): String {
        // monthString is likely "YYYY-MM" (e.g. "2026-05")
        return try {
            val parts = monthString.split("-")
            if (parts.size >= 2) {
                val monthIndex = parts[1].toInt()
                val monthNames = listOf("", "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
                monthNames.getOrElse(monthIndex) { monthString.take(3).uppercase() }
            } else {
                monthString.take(3).uppercase()
            }
        } catch (_: Exception) {
            monthString.take(3).uppercase()
        }
    }

    private fun formatCurrency(amount: Double): String {
        return amount.toInt().toString().reversed().chunked(3).joinToString(",").reversed()
    }

    private fun formatLargeNumber(amount: Double): String {
        return if (amount >= 1_000_000) {
            "${(amount / 1_000_000.0 * 10).toInt() / 10.0}M"
        } else if (amount >= 1_000) {
            "${(amount / 1_000.0).toInt()}k"
        } else {
            amount.toInt().toString()
        }
    }
}
