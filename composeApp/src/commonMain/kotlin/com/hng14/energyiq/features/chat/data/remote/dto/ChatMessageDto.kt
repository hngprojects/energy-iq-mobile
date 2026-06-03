package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val content: String,
    val contentType: String? = null,
    val deliveryStatus: String? = null,
    val isTransitioning: Boolean? = null,
    val senderId: String,
)

