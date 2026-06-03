package com.hng14.energyiq.features.auth.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: String,
    val emailVerified: Boolean,
    val onBoardingComplete: Boolean? = false,
    val inverterBrand: String? = null,
    val businessName: String? = null,
    val businessType: String? = null,
    val state: String? = null,
    val city: String? = null,
    val aiLanguage: String? = null,
    val profileUrl: String? = null,
)
