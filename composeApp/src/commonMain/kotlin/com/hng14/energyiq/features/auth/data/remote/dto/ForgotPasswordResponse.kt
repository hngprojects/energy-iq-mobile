package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
    val data: ForgotPasswordDataDto,
    val meta: ResponseMetaDto,
)

@Serializable
data class ForgotPasswordDataDto(
    val email: String,
)
