package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(
    val startingMessage: String? = null,
)
