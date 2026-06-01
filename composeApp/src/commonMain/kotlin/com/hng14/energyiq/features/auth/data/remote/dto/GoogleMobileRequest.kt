package com.hng14.energyiq.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleMobileRequest(
    val idToken: String,
)

