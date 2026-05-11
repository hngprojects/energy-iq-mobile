package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val success: Boolean,
    val message: String,
    val error: String,
    val statusCode: Int,
    val meta: ResponseMetaDto,
)
