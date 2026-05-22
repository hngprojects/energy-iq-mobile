package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiErrorResponse(
    val success: Boolean,
    val message: JsonElement,
    val error: String,
    val statusCode: Int,
    val meta: ResponseMetaDto,
)
