package com.hng14.energyiq.features.onboarding.data.remote.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class ConnectInverterRequest(
    val brand: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val victronAccessToken: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val growattApiToken: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val solarmanEmail: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val solarmanPassword: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val sandboxAccessToken: String? = null,
)
