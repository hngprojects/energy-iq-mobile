package com.hng14.energyiq.features.alerts.data.remote.dto

import com.hng14.energyiq.features.auth.data.remote.dto.ResponseMetaDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AlertsResponse(
    val success: Boolean,
    val message: JsonElement,
    val data: JsonElement? = null,
    val meta: ResponseMetaDto,
)

