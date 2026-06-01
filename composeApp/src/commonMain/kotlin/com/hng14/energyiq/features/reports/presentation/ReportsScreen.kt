package com.hng14.energyiq.features.reports.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hng14.energyiq.features.home.presentation.components.HomeTopBar
import com.hng14.energyiq.features.reports.domain.model.ReportIcon
import com.hng14.energyiq.features.reports.domain.model.ReportItem
import com.hng14.energyiq.features.reports.domain.model.ReportStat
import com.hng14.energyiq.features.reports.domain.model.ReportStatus
import com.hng14.energyiq.features.reports.presentation.components.ReportCard
import com.hng14.energyiq.features.reports.presentation.components.ReportStatCard
import com.hng14.energyiq.features.reports.presentation.components.ReportToolbar

@Composable
fun ReportsScreen(
    name: String,
    onViewReport: (String) -> Unit,
    onDownloadReport: (String) -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val stats = rememberReportStats()
    val reports = rememberReportItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        HomeTopBar(
            name = name,
            onProfileClick = onProfileClick
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            ReportStatsGrid(stats = stats)
            Spacer(modifier = Modifier.height(24.dp))
            ReportToolbar(onGenerateReport = { /* TODO */ })
            Spacer(modifier = Modifier.height(24.dp))
            reports.forEachIndexed { index, item ->
                ReportCard(
                    item = item,
                    onView = onViewReport,
                    onDownload = onDownloadReport,
                )
                if (index != reports.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
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
            status = ReportStatus.READY,
            icon = ReportIcon.SOLAR,
            title = "Solar Performance - May Wk 1",
            dateRange = "1 - 5 May",
        ),
        ReportItem(
            id = "report-2",
            status = ReportStatus.READY,
            icon = ReportIcon.ENERGY,
            title = "Week 3 Energy Report",
            dateRange = "7 April - 9 May",
        ),
        ReportItem(
            id = "report-3",
            status = ReportStatus.READY,
            icon = ReportIcon.SUMMARY,
            title = "April Monthly Summary",
            dateRange = "April 2026",
        ),
        ReportItem(
            id = "report-4",
            status = ReportStatus.READY,
            icon = ReportIcon.ENERGY,
            title = "Alert Digest - Week 2",
            dateRange = "20 - 26 April",
        ),
        ReportItem(
            id = "report-5",
            status = ReportStatus.READY,
            icon = ReportIcon.ENERGY,
            title = "Week 2 Energy Report",
            dateRange = "20 - 26 April",
        ),
        ReportItem(
            id = "report-6",
            status = ReportStatus.READY,
            icon = ReportIcon.BREAKDOWN,
            title = "Device Consumption Breakdown",
            dateRange = "27 April - 3 May",
        ),
        ReportItem(
            id = "report-7",
            status = ReportStatus.READY,
            icon = ReportIcon.ENERGY,
            title = "Week 1 Energy Report",
            dateRange = "31 - 1 April",
        ),
    )
}
