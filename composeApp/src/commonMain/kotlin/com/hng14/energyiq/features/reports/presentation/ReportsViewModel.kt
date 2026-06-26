package com.hng14.energyiq.features.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.home.data.HomeRepository
import com.hng14.energyiq.features.reports.data.ReportsRepository
import com.hng14.energyiq.features.reports.domain.model.ReportIcon
import com.hng14.energyiq.features.reports.domain.model.ReportItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ReportsState(
    val reports: List<ReportItem> = emptyList(),
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val selectedType: String = "ALERT",
    val selectedPeriod: String = "weekly", // "weekly", "monthly", "custom"
    val startDate: String? = null,
    val endDate: String? = null
)

class ReportsViewModel(
    private val reportsRepository: ReportsRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState(reports = emptyList()))
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val fetchedReports = reportsRepository.getReports()
                _state.update { 
                    it.copy(
                        reports = fetchedReports,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load reports"
                    ) 
                }
            }
        }
    }

    fun selectReportType(type: String) {
        _state.update { it.copy(selectedType = type) }
    }

    fun selectPeriod(period: String) {
        _state.update { 
            it.copy(
                selectedPeriod = period,
                startDate = if (period == "custom") it.startDate else null,
                endDate = if (period == "custom") it.endDate else null
            ) 
        }
    }

    fun selectCustomDateRange(start: String, end: String) {
        _state.update { it.copy(startDate = start, endDate = end) }
    }

    fun generateReport() {
        if (_state.value.isGenerating) return

        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, errorMessage = null, successMessage = null) }
            try {
                val inverterId = homeRepository.peekSessionInverterId() 
                    ?: homeRepository.getSelectedInverterId()
                    ?: throw Exception("No active inverter session found. Please connect your inverter first.")
                
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                val reportType = _state.value.selectedType
                val backendType = if (reportType == "Cost And Savings") "COSTS_AND_SAVINGS" else reportType

                val reportName = when (reportType) {
                    "SOLAR" -> "Solar Performance - $today"
                    "ALERT" -> "Alert Digest - $today"
                    "Cost And Savings" -> "Cost and Savings - $today"
                    else -> "General Summary - $today"
                }

                val period = _state.value.selectedPeriod
                val isCustom = period == "custom"
                val mode = if (isCustom) "custom-date" else "period"
                
                val start = if (isCustom) _state.value.startDate else today
                val end = if (isCustom) _state.value.endDate else today

                if (isCustom && (start.isNullOrBlank() || end.isNullOrBlank())) {
                    throw Exception("Please select a valid start and end date for the custom range.")
                }

                val newReport = reportsRepository.createReport(
                    mode = mode,
                    inverterId = inverterId,
                    type = backendType,
                    name = reportName,
                    period = period,
                    referenceDate = today,
                    startDate = start,
                    endDate = end
                )

                _state.update {
                    it.copy(
                        reports = it.reports + newReport,
                        isGenerating = false,
                        successMessage = "Report generated successfully"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = e.message ?: "Failed to generate report"
                    )
                }
            }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun deleteReport(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                reportsRepository.deleteReport(id)
                _state.update { it.copy(successMessage = "Report deleted successfully") }
                loadReports()
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to delete report"
                    ) 
                }
            }
        }
    }

    fun emailReport(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, errorMessage = null, successMessage = null) }
            try {
                reportsRepository.emailReport(id)
                _state.update { 
                    it.copy(
                        isGenerating = false,
                        successMessage = "Report has been sent to your email"
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isGenerating = false,
                        errorMessage = e.message ?: "Failed to email report"
                    ) 
                }
            }
        }
    }

    fun cancelReport(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                reportsRepository.cancelReport(id)
                _state.update { it.copy(successMessage = "Report generation cancelled successfully") }
                loadReports()
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to cancel report"
                    ) 
                }
            }
        }
    }

    private fun getDefaultReports(): List<ReportItem> {
        return listOf(
            ReportItem(
                id = "report-1",
                status = "READY",
                icon = ReportIcon.SOLAR,
                title = "Solar Performance - May Wk 1",
                dateRange = "1 - 5 May",
            ),
            ReportItem(
                id = "report-2",
                status = "READY",
                icon = ReportIcon.ENERGY,
                title = "Week 3 Energy Report",
                dateRange = "7 April - 9 May",
            ),
            ReportItem(
                id = "report-3",
                status = "READY",
                icon = ReportIcon.SUMMARY,
                title = "April Monthly Summary",
                dateRange = "April 2026",
            ),
            ReportItem(
                id = "report-4",
                status = "READY",
                icon = ReportIcon.ENERGY,
                title = "Alert Digest - Week 2",
                dateRange = "20 - 26 April",
            ),
            ReportItem(
                id = "report-5",
                status = "READY",
                icon = ReportIcon.ENERGY,
                title = "Week 2 Energy Report",
                dateRange = "20 - 26 April",
            ),
            ReportItem(
                id = "report-6",
                status = "READY",
                icon = ReportIcon.BREAKDOWN,
                title = "Device Consumption Breakdown",
                dateRange = "27 April - 3 May",
            ),
            ReportItem(
                id = "report-7",
                status = "READY",
                icon = ReportIcon.ENERGY,
                title = "Week 1 Energy Report",
                dateRange = "31 - 1 April",
            ),
        )
    }
}
