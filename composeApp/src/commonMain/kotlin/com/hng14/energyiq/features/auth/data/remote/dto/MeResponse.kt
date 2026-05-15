package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val success: Boolean,
    val message: String,
    val data: UserDto,
    val meta: ResponseMetaDto,
)
