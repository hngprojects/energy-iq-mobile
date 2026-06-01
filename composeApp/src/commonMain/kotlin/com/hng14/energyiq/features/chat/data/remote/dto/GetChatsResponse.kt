package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetChatsResponse(
    val success: Boolean,
    val message: String,
    val data: List<ChatDto>,
)

