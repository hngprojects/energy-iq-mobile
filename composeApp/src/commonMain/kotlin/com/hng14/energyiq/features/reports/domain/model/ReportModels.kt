package com.hng14.energyiq.features.reports.domain.model

import androidx.compose.ui.graphics.Color

data class ReportStat(
    val title: String,
    val value: String,
    val percentageChange: String,
    val subtitle: String,
    val dotColor: Color,
    val showSunIcon: Boolean = false,
    val showReportSentIcon: Boolean = false,
    val showBatteryChargingIcon: Boolean = false,
    val showDangerVectorIcon: Boolean = false,
)

enum class ReportIcon {
    SOLAR,
    ENERGY,
    SUMMARY,
    BREAKDOWN,
}

data class ReportItem(
    val id: String,
    val status: String,
    val icon: ReportIcon,
    val title: String,
    val dateRange: String,
)
