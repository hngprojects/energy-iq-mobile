package com.hng14.energyiq.features.alerts.data

import com.hng14.energyiq.features.alerts.data.remote.AlertsApi
import com.hng14.energyiq.features.alerts.domain.model.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class AlertRepository(
    private val api: AlertsApi,
) {
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

    suspend fun fetchAlerts(
        alertType: AlertType?,
        pageNumber: Int,
        pageSize: Int,
    ): List<SmartAlertItem> {
        val response = api.fetchAlerts(
            alertType = alertType?.apiValue,
            pageNumber = pageNumber,
            pageSize = pageSize,
        )

        val items = extractAlertItems(response.data)
        if (items.isEmpty()) return emptyList()

        return items.mapIndexed { index, element -> element.toSmartAlertItem(fallbackId = "alert-$index") }
    }

    private fun extractAlertItems(data: JsonElement?): List<JsonObject> {
        if (data == null) return emptyList()
        return when (data) {
            is JsonArray -> data.mapNotNull { it as? JsonObject }
            is JsonObject -> {
                val inner = data["alerts"] ?: data["items"] ?: data["data"]
                when (inner) {
                    is JsonArray -> inner.mapNotNull { it as? JsonObject }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
    }

    private fun JsonObject.toSmartAlertItem(fallbackId: String): SmartAlertItem {
        val id = string("id")
            ?: string("_id")
            ?: string("alertId")
            ?: fallbackId
        val title = string("title")
            ?: string("name")
            ?: string("type")
            ?: "Alert"
        val description = string("message")
            ?: string("description")
            ?: string("details")
            ?: ""
        val rawSeverity = string("severity")
            ?: string("level")
            ?: string("priority")
        val severity = when (rawSeverity?.uppercase()) {
            "CRITICAL", "HIGH" -> AlertSeverity.CRITICAL
            "WARNING", "MEDIUM" -> AlertSeverity.WARNING
            "SUCCESS", "INFO", "LOW" -> AlertSeverity.SUCCESS
            else -> AlertSeverity.WARNING
        }

        val resolved = boolean("resolved")
            ?: boolean("isResolved")
            ?: (string("status")?.uppercase() == "RESOLVED")

        val icon = when {
            title.contains("battery", ignoreCase = true) || description.contains("battery", ignoreCase = true) -> AlertCardIcon.BATTERY
            title.contains("solar", ignoreCase = true) || description.contains("solar", ignoreCase = true) -> AlertCardIcon.SOLAR
            title.contains("hvac", ignoreCase = true) || description.contains("ac", ignoreCase = true) -> AlertCardIcon.HVAC
            else -> AlertCardIcon.AUTOMATION
        }

        val timestamp = string("createdAt")
            ?: string("timestamp")
            ?: ""

        return SmartAlertItem(
            id = id,
            severity = severity,
            title = title,
            description = description,
            timestamp = timestamp,
            actionLabel = if (resolved) "Resolved" else "Inspect",
            resolved = resolved,
            icon = icon,
        )
    }

    private fun JsonObject.string(key: String): String? {
        val value = this[key] as? JsonPrimitive ?: return null
        return runCatching { value.content }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun JsonObject.boolean(key: String): Boolean? {
        val value = this[key] as? JsonPrimitive ?: return null
        // Compatibility: kotlinx.serialization versions differ on booleanOrNull availability.
        val raw = runCatching { value.content }.getOrNull()?.trim().orEmpty()
        return when (raw.lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }
    }
}
