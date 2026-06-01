package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    val contextLength: Int? = null,
    val expirationTimeoutSeconds: Int? = null,
    val roomId: String,
    val userId: String,
    val lastMessageTimestamp: String? = null,
    val lastMessagePreview: String? = null,
    val title: String? = null,
    val id: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null,
    val isActive: Boolean? = null,
    val isArchived: Boolean? = null,
)
