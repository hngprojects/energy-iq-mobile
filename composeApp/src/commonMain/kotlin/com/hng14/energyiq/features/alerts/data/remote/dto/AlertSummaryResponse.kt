package com.hng14.energyiq.features.alerts.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AlertSummaryResponse(
    val success: Boolean,
    val message: String,
    val data: AlertSummaryData,
    val meta: JsonElement? = null,
)

@Serializable
data class AlertSummaryData(
    val active: Int,
    val critical: Int,
    val unresolved: Int,
    val warning: Int,
)
