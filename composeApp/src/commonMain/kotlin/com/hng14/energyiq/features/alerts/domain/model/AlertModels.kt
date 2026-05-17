package com.hng14.energyiq.features.alerts.domain.model

import androidx.compose.ui.graphics.Color

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    SUCCESS,
}

enum class AlertCardIcon {
    BATTERY,
    FRIDGE,
    HVAC,
    AUTOMATION,
    SOLAR,
}

enum class AlertFilter(
    val label: String,
) {
    ALL("All"),
    SUCCESS("Success"),
    WARNING("Warning"),
    CRITICAL("Critical"),
    RESOLVED("Resolved"),
    UNRESOLVED("Unresolved"),
}

data class AlertStat(
    val title: String,
    val value: String,
    val subtitle: String,
    val dotColor: Color,
)

data class SmartAlertItem(
    val id: String,
    val severity: AlertSeverity,
    val title: String,
    val description: String,
    val timestamp: String,
    val actionLabel: String,
    val resolved: Boolean = false,
    val icon: AlertCardIcon,
)

data class AlertMetric(
    val label: String,
    val value: String,
)

data class SmartAlertDialogContent(
    val title: String,
    val description: String,
    val timestamp: String,
    val metrics: List<AlertMetric>,
    val explanation: String,
    val primaryActionLabel: String,
)

data class SeverityPalette(
    val label: String,
    val background: Color,
    val foreground: Color,
    val dot: Color,
)
