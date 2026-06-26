package com.hng14.energyiq.features.reports.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import com.hng14.energyiq.core.ui.EmptyStateCard
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.ServerErrorDialog
import com.hng14.energyiq.core.ui.SuccessDialog
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.hng14.energyiq.features.reports.domain.model.ReportIcon
import com.hng14.energyiq.features.reports.domain.model.ReportItem
import com.hng14.energyiq.features.reports.domain.model.ReportStat
import com.hng14.energyiq.features.reports.presentation.components.ReportCard
import com.hng14.energyiq.features.reports.presentation.components.ReportStatCard
import com.hng14.energyiq.features.reports.presentation.components.ReportToolbar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    name: String,
    onDeleteReport: (String) -> Unit,
    onDownloadReport: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReportsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val stats = rememberReportStats()
    val reports = state.reports
    val dmSans = dmSansFontFamily()

    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    var deleteReportId by remember { mutableStateOf<String?>(null) }
    var cancelReportId by remember { mutableStateOf<String?>(null) }

    if (deleteReportId != null) {
        ConfirmationDialog(
            title = "Delete Report",
            message = "Are you sure you want to delete this report? This action cannot be undone.",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = {
                deleteReportId?.let { viewModel.deleteReport(it) }
                deleteReportId = null
            },
            onDismiss = { deleteReportId = null },
            confirmButtonColor = Color(0xFFEF4444)
        )
    }

    if (cancelReportId != null) {
        ConfirmationDialog(
            title = "Cancel Report",
            message = "Are you sure you want to cancel generating this report? This action cannot be undone.",
            confirmText = "Cancel Report",
            dismissText = "Keep Generating",
            onConfirm = {
                cancelReportId?.let { viewModel.cancelReport(it) }
                cancelReportId = null
            },
            onDismiss = { cancelReportId = null },
            confirmButtonColor = Color(0xFFEF4444)
        )
    }

    val customDateRangeLabel = if (state.startDate != null && state.endDate != null) {
        "${formatIsoToFriendly(state.startDate)} - ${formatIsoToFriendly(state.endDate)}"
    } else null

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            viewModel.selectCustomDateRange(
                                start = formatMillisToIso(start),
                                end = formatMillisToIso(end)
                            )
                        }
                        showDateRangePicker = false
                    }
                ) {
                    Text("Confirm", color = Color(0xFF111827))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel", color = Color(0xFF6B7280))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
            )
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f),
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = Color(0xFF111827),
                    selectedDayContentColor = Color.White,
                    dayInSelectionRangeContainerColor = Color(0xFF111827).copy(alpha = 0.1f),
                    todayDateBorderColor = Color(0xFF111827),
                    todayContentColor = Color(0xFF111827),
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF111827),
                    headlineContentColor = Color(0xFF111827),
                    weekdayContentColor = Color(0xFF6B7280),
                    subheadContentColor = Color(0xFF6B7280)
                )
            )
        }
    }

    if (state.successMessage != null) {
        SuccessDialog(
            message = state.successMessage!!,
            onDone = { viewModel.dismissMessage() }
        )
    }

    if (state.errorMessage != null) {
        ServerErrorDialog(
            message = state.errorMessage!!,
            onDismiss = { viewModel.dismissMessage() }
        )
    }

    if (state.isGenerating || (state.isLoading && reports.isEmpty())) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF111827))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827),
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Reports",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Color(0xFF111827),
            )
        }
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadReports() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
            ReportStatsGrid(stats = stats)
            Spacer(modifier = Modifier.height(24.dp))
            ReportToolbar(
                selectedType = state.selectedType,
                onTypeSelected = viewModel::selectReportType,
                selectedPeriod = state.selectedPeriod,
                onPeriodSelected = viewModel::selectPeriod,
                customDateRangeLabel = customDateRangeLabel,
                onShowDatePicker = { showDateRangePicker = true },
                onGenerateReport = { viewModel.generateReport() }
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (reports.isEmpty()) {
                EmptyStateCard(
                    title = "No reports generated yet",
                    description = "Generate your first report to track your system's efficiency, consumption, and performance.",
                    icon = Icons.Outlined.Analytics,
                    buttonText = "Generate Report",
                    onRetry = { viewModel.generateReport() }
                )
            } else {
                reports.forEachIndexed { index, item ->
                    ReportCard(
                        item = item,
                        onDelete = { id -> deleteReportId = id },
                        onDownload = { id -> viewModel.emailReport(id) },
                        onCancel = { id -> cancelReportId = id },
                    )
                    if (index != reports.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ReportStatsGrid(
    stats: List<ReportStat>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowStats.forEach { stat ->
                    ReportStatCard(
                        stat = stat,

                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberReportStats(): List<ReportStat> {
    return listOf(
        ReportStat(
            title = "Generated",
            value = "1,248kwh",
            subtitle = " vs April",
            dotColor = Color(0xFFF59E0B),
            showSunIcon = true,
            percentageChange = "↑  14%",
        ),
        ReportStat(
            title = "Reports Sent",
            value = "12",
            subtitle = "5 recipients",
            dotColor = Color(0xFF3B82F6),
            showReportSentIcon = true,
            percentageChange = ""
        ),
        ReportStat(
            title = "Bat. Efficiency",
            value = "91%",
            subtitle = " vs April",
            dotColor = Color(0xFF22C55E),
            percentageChange = "↑  3%",
            showBatteryChargingIcon = true,
        ),
        ReportStat(
            title = "Resolved",
            value = "11/14",
            subtitle = "79% resolution rate",
            dotColor = Color(0xFF6366F1),
            percentageChange = "",
            showDangerVectorIcon = true,
        ),
    )
}

@Composable
private fun rememberReportItems(): List<ReportItem> {
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

private fun formatMillis(millis: Long?): String {
    if (millis == null) return ""
    val instant = Instant.fromEpochMilliseconds(millis)
    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$monthName ${date.dayOfMonth}, ${date.year}"
}

private fun formatMillisToIso(millis: Long?): String {
    if (millis == null) return ""
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
}

private fun formatIsoToFriendly(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        val date = kotlinx.datetime.LocalDate.parse(iso)
        val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        "$monthName ${date.dayOfMonth}, ${date.year}"
    } catch (e: Exception) {
        iso
    }
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonColor: Color,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = dmSansFontFamily()
                ),
                color = Color(0xFF111827)
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSansFontFamily()
                ),
                color = Color(0xFF4B5563)
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(dismissText, color = Color(0xFF374151), fontFamily = dmSansFontFamily())
                }
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(confirmText, fontFamily = dmSansFontFamily())
                }
            }
        },
        dismissButton = null,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
    )
}
