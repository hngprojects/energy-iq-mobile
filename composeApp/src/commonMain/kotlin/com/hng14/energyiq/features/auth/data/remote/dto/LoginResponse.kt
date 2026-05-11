package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginDataDto,
    val meta: ResponseMetaDto,
)

@Serializable
data class LoginDataDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto,
)

