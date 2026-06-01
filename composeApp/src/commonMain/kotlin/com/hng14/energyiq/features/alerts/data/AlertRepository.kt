package com.hng14.energyiq.features.alerts.data

import com.hng14.energyiq.core.storage.PreferenceStore
import com.hng14.energyiq.features.alerts.data.remote.AlertsApi
import com.hng14.energyiq.features.alerts.domain.model.AlertCardIcon
import com.hng14.energyiq.features.alerts.domain.model.AlertMetric
import com.hng14.energyiq.features.alerts.domain.model.AlertSeverity
import com.hng14.energyiq.features.alerts.domain.model.AlertType
import com.hng14.energyiq.features.alerts.domain.model.SmartAlertDialogContent
import com.hng14.energyiq.features.alerts.domain.model.SmartAlertItem
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class AlertRepository(
    private val api: AlertsApi,
    private val store: PreferenceStore,
    private val json: Json,
    private val authPrefs: AuthPreferences
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

    fun buildSmartAlertDialogContent(alert: SmartAlertItem): SmartAlertDialogContent {
        // For the specific mock item that has rich details
        if (alert.id == "battery-critically-low-3") {
            return SmartAlertDialogContent(
                title = "Battery critically low",
                description = "Charge level at 11%.",
                timestamp = alert.timestamp,
                metrics = listOf(
                    AlertMetric("Battery\nSOC", "11%"),
                    AlertMetric("Discharge\nrate", "1.4 KW"),
                    AlertMetric("Time to\n0%", "2h 30m"),
                ),
                explanation = "Your battery reserve dropped below the 15% threshold at 5:42 am. Solar generated so far has not made up for the overnight draw. Immediate action recommended to avoid outage.",
                primaryActionLabel = "Resolve Now",
            )
        }

        // Generic details for other alerts
        return SmartAlertDialogContent(
            title = alert.title,
            description = alert.description,
            timestamp = alert.timestamp,
            metrics = listOf(
                AlertMetric("Severity", alert.severity.name.lowercase().replaceFirstChar { it.uppercase() }),
                AlertMetric("Status", if (alert.resolved) "Resolved" else "Open"),
                AlertMetric("Category", "System"),
            ),
            explanation = "This alert was raised because the system detected unusual behavior that needs review. Please inspect your system components.",
            primaryActionLabel = if (alert.resolved) "View Details" else "Resolve Now",
        )
    }

    private companion object {
        const val ALERTS_CACHE_KEY = "alerts_list_cache"
        const val SUMMARY_CACHE_KEY = "alerts_summary_cache"
    }

    suspend fun fetchAlerts(
        alertType: AlertType?,
    ): List<SmartAlertItem> {
        return try {
            val response = api.fetchAlerts(alertType = alertType?.apiValue)
            // User-scoped key for the alerts list
            val scopedKey = authPrefs.getUserScopedKey(ALERTS_CACHE_KEY)
            store.put(
                scopedKey,
                json.encodeToString(
                    com.hng14.energyiq.features.alerts.data.remote.dto.AlertsResponse.serializer(),
                    response
                )
            )

            val items = extractAlertItems(response.data)
            items.mapIndexed { index, element -> element.toSmartAlertItem(fallbackId = "alert-$index") }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchAlertSummary(): com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryResponse {
        return try {
            val response = api.fetchAlertSummary()
            // User-scoped key for the summary stats
            val scopedKey = authPrefs.getUserScopedKey(SUMMARY_CACHE_KEY)
            store.put(scopedKey, json.encodeToString(com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryResponse.serializer(), response))
            response
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getCachedAlerts(alertType: AlertType?): List<SmartAlertItem>? {
        return try {
            val scopedKey = authPrefs.getUserScopedKey(ALERTS_CACHE_KEY)
            val cachedJson = store.get(scopedKey) ?: return null
            val response = json.decodeFromString<com.hng14.energyiq.features.alerts.data.remote.dto.AlertsResponse>(cachedJson)
            val items = extractAlertItems(response.data)
            items.mapIndexed { index, element -> element.toSmartAlertItem(fallbackId = "alert-$index") }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getCachedAlertSummary(): com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryResponse? {
        return try {
            val cachedJson = store.get(SUMMARY_CACHE_KEY) ?: return null
            json.decodeFromString<com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryResponse>(cachedJson)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun resolveAlert(alertId: String) {
        api.resolveAlert(alertId)
    }

    private fun extractAlertItems(data: JsonElement?): List<JsonObject> {
        if (data == null) return emptyList()
        return when (data) {
            is JsonArray -> data.mapNotNull { it as? JsonObject }
            is JsonObject -> {
                val inner = data["alerts"] ?: data["items"] ?: data["data"]
                when (inner) {
                    is JsonArray -> inner.mapNotNull { it as? JsonObject }
                    else -> listOf(data) // Try treating the object itself as the item if it's not a wrapper
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
        val title = string("type")?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.titlecase() }
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

        val resolved = (string("resolutionStatus")?.uppercase() == "RESOLVED")
            || (boolean("resolved") == true)
            || (boolean("isResolved") == true)
            || (string("status")?.uppercase() == "RESOLVED")

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
            timestamp = formatAlertTimestamp(timestamp),
            actionLabel = if (resolved) "Resolved" else "Inspect",
            resolved = resolved,
            icon = icon,
        )
    }

    private fun formatAlertTimestamp(isoString: String): String {
        if (isoString.isBlank()) return ""
        return try {
            val instant = kotlin.time.Instant.parse(isoString)
            val now = kotlin.time.Clock.System.now()
            val tz = TimeZone.currentSystemDefault()
            val dateTime = instant.toLocalDateTime(tz)
            val today = now.toLocalDateTime(tz).date
            val datePart = when (val alertDate = dateTime.date) {
                today -> "Today"
                today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> "Yesterday"
                else -> "${alertDate.day} ${
                    alertDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
                }"
            }

            val hour = dateTime.hour
            val minute = dateTime.minute
            val amPm = if (hour < 12) "am" else "pm"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val displayMinute = minute.toString().padStart(2, '0')

            "$datePart, $displayHour:$displayMinute $amPm"
        } catch (_: Exception) {
            isoString
        }
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
