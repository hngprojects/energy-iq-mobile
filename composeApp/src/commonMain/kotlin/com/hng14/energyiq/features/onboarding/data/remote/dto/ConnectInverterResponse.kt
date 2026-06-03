package com.hng14.energyiq.features.onboarding.data.remote.dto

import com.hng14.energyiq.features.auth.data.remote.dto.ResponseMetaDto
import kotlinx.serialization.Serializable

@Serializable
data class ConnectInverterResponse(
    val success: Boolean,
    val message: String,
    val data: InverterDto,
    val meta: ResponseMetaDto,
)

@Serializable
data class InverterDto(
    val id: String,
    val brand: String,
    val model: String? = null,
)
