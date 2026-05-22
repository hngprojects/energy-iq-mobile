package com.hng14.energyiq.features.onboarding.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectInverterRequest(
    val brand: String,
    val victronAccessToken: String? = null,
    val growattApiToken: String? = null,
    val solarmanEmail: String? = null,
    val solarmanPassword: String? = null,
    val sandboxAccessToken: String? = null,
)
