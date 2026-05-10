package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)