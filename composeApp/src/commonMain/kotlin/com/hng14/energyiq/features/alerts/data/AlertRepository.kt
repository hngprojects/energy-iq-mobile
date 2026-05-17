package com.hng14.energyiq.features.alerts.data

import androidx.compose.ui.graphics.Color
import com.hng14.energyiq.features.alerts.domain.model.*

class AlertRepository {
    val smartAlertStats = listOf(
        AlertStat("Active Alerts", "14", "Last 7 days", Color(0xFF84CC16)),
        AlertStat("Critical", "1", "Need action now", Color(0xFFEF4444)),
        AlertStat("Warning", "3", "Awaiting review", Color(0xFFF59E0B)),
        AlertStat("Unresolved", "5", "Still open", Color(0xFF111827)),
    )

    val smartAlertItems = listOf(
        SmartAlertItem(
            id = "battery-critically-low-3",
            severity = AlertSeverity.CRITICAL,
            title = "Battery critically low",
            description = "Charge level at 1%",
            timestamp = "Today, 6:10 am",
            actionLabel = "Inspect",
            icon = AlertCardIcon.BATTERY,
        ),
        SmartAlertItem(
            id = "fridge-drawing-high-power",
            severity = AlertSeverity.WARNING,
            title = "Fridge drawing high power",
            description = "4.3kWh consumed in 2 hours",
            timestamp = "Today, 3:00 am",
            actionLabel = "Inspect",
            icon = AlertCardIcon.FRIDGE,
        ),
        SmartAlertItem(
            id = "hvac-running-outside-schedule",
            severity = AlertSeverity.WARNING,
            title = "HVAC running outside sched...",
            description = "AC running 3 hours after scheduled...",
            timestamp = "Yesterday, 5:00 am",
            actionLabel = "Resolved",
            resolved = true,
            icon = AlertCardIcon.HVAC,
        ),
        SmartAlertItem(
            id = "battery-fully-charged",
            severity = AlertSeverity.SUCCESS,
            title = "Battery fully charged",
            description = "Your battery has reached 100%",
            timestamp = "6 May, 5:00 pm",
            actionLabel = "Resolved",
            resolved = true,
            icon = AlertCardIcon.BATTERY,
        ),
        SmartAlertItem(
            id = "automation-rule-updated",
            severity = AlertSeverity.SUCCESS,
            title = "Automation rule updated...",
            description = "Your HVAC eco rule adjusted by AI...",
            timestamp = "5 May, 4:20 pm",
            actionLabel = "Resolved",
            resolved = true,
            icon = AlertCardIcon.AUTOMATION,
        ),
        SmartAlertItem(
            id = "solar-generation-below-threshold",
            severity = AlertSeverity.WARNING,
            title = "Solar generation 48% below...",
            description = "Possible panel shading or inverter...",
            timestamp = "4 May, 6:30 pm",
            actionLabel = "Resolved",
            resolved = true,
            icon = AlertCardIcon.SOLAR,
        ),
    )

    fun buildSmartAlertDialogContent(alertId: String): SmartAlertDialogContent? = when (alertId) {
        "battery-critically-low-3" -> SmartAlertDialogContent(
            title = "Battery critically low",
            description = "Charge level at 11%.",
            timestamp = "Today, 6:10am",
            metrics = listOf(
                AlertMetric("Battery\nSOC", "11%"),
                AlertMetric("Discharge\nrate", "1.4 KW"),
                AlertMetric("Time to\n0%", "2h 30m"),
            ),
            explanation = "Your battery reserve dropped below the 15% threshold at 5:42 am. Solar generated so far has not made up for the overnight draw. Immediate action recommended to avoid outage.",
            primaryActionLabel = "Resolve Now",
        )
        else -> SmartAlertDialogContent(
            title = "Alert details",
            description = smartAlertItems.firstOrNull { it.id == alertId }?.description ?: "",
            timestamp = smartAlertItems.firstOrNull { it.id == alertId }?.timestamp ?: "",
            metrics = listOf(
                AlertMetric("Severity", smartAlertItems.firstOrNull { it.id == alertId }?.severity?.name ?: ""),
                AlertMetric("Status", if (smartAlertItems.firstOrNull { it.id == alertId }?.resolved == true) "Resolved" else "Open"),
                AlertMetric("Category", "System"),
            ),
            explanation = "This alert was raised because the system detected unusual behavior that needs review.",
            primaryActionLabel = "Resolve Now",
        )
    }
}
