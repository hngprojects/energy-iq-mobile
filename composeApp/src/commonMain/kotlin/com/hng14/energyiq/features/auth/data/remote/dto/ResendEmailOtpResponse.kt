package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResendEmailOtpResponse(
    val success: Boolean,
    val message: String,
    val meta: ResponseMetaDto,
)

