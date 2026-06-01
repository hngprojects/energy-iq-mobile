package com.hng14.energyiq.features.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val emailVerified: Boolean,
    val onBoardingComplete: Boolean,
    val inverterBrand: String? = null,
    val businessName: String? = null,
    val businessType: String? = null,
    val state: String? = null,
    val city: String? = null,
    val aiLanguage: String? = null,
    val profileUrl: String? = null,
)
