package dev.logickoder.starter.features.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("user") val user: UserDto,
)
