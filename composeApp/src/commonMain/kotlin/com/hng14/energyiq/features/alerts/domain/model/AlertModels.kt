package com.hng14.energyiq.features.alerts.domain.model

import androidx.compose.ui.graphics.Color

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    SUCCESS,
}

enum class AlertType(
    val apiValue: String,
    val label: String,
) {
    BATTERY_PERCENTAGE("BATTERY_PERCENTAGE", "Battery %"),
    LOW_BATTERY("LOW_BATTERY", "Low Battery"),
    BATTERY_TEMPERATURE("BATTERY_TEMPERATURE", "Battery Temp"),
    ENERGY("ENERGY", "Energy"),
    OTHER("OTHER", "Other"),
    POWER("POWER", "Power"),
    SOLAR_GEN("SOLAR_GEN", "Solar Gen"),
    INVERTER_FAULT("INVERTER_FAULT", "Inverter Fault"),
    INVERTER_TEMPERATURE("INVERTER_TEMPERATURE", "Inverter Temp"),
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
