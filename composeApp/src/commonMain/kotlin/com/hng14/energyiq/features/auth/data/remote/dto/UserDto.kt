package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val googleId: String? = null,
    val inverterBrand: String? = null,
    val onboardingStep: Int? = null,
    val onboardingComplete: Boolean? = null,
    val isActive: Boolean? = null,
    val lastLoginAt: String? = null,
    val role: String,
    val emailVerified: Boolean,
    val businessName: String? = null,
    val businessType: String? = null,
    val state: String? = null,
    val city: String? = null,
    val aiLanguage: String? = null,
    val profileUrl: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
