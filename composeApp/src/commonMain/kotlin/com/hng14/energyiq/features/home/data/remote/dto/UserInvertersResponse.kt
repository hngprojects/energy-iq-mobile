package com.hng14.energyiq.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInvertersResponse(
    val success: Boolean,
    val message: String,
    val data: List<InverterDto>,
)

@Serializable
data class InverterDto(
    val id: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val userId: String? = null,
    val brand: String,
    val model: String? = null,
    val serialNumber: String? = null,
    val installationId: String? = null,
    val apiType: String? = null,
    val isActive: Boolean = true,
    val isOffline: Boolean? = null,
    val lastSyncedAt: String? = null,
    val ratedCapacityKwh: String? = null,
    val panelCapacityKw: String? = null,
)
