package com.hng14.energyiq.features.chat.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GetChatMessagesResponse(
    val success: Boolean = false,
    val message: String? = null,
    /**
     * Backend response shape has been observed to vary (sometimes `data` is missing or is an object).
     * Keep it flexible and let the repository extract the messages array.
     */
    val data: JsonElement? = null,
    // Some backends may return messages at the top level.
    val messages: List<ChatMessageDto>? = null,
)
