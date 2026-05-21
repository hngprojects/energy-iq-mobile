package com.hng14.energyiq.features.onboarding.data.remote.dto

import com.hng14.energyiq.features.auth.data.remote.dto.ResponseMetaDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ConnectInverterResponse(
    val success: Boolean,
    val message: String,
    val data: JsonElement? = null,
    val meta: ResponseMetaDto,
)

