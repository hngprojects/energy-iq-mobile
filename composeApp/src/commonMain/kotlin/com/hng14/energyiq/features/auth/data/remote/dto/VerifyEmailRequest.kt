package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    @SerialName("email") val email: String,
    @SerialName("otp") val otp: String,
)