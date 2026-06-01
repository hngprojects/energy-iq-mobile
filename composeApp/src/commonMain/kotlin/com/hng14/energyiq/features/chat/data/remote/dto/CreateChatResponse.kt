package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatResponse(
    val success: Boolean,
    val message: String,
    val data: ChatDto,
)

