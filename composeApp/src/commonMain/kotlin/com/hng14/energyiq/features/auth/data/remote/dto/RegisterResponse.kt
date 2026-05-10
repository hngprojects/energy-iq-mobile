package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: RegisterUserDto,
    val meta: ResponseMetaDto
)

@Serializable
data class RegisterUserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val emailVerified: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class ResponseMetaDto(
    val timestamp: String,
)