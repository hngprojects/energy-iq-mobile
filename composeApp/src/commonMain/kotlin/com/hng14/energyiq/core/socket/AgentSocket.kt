package com.hng14.energyiq.core.socket

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface SocketConnectionState {
    data object Disconnected : SocketConnectionState
    data object Connecting : SocketConnectionState
    data object Connected : SocketConnectionState
}

data class AgentSocketError(
    val code: String,
    val message: String,
)

data class JoinedChatsEvent(
    val userId: String,
    val field2: Int? = null,
    val field3: Boolean? = null,
    // Server payload has been observed to sometimes be an array; keep raw for debugging/forward compat.
    val rawPayload: String? = null,
)

data class SendMsgPayload(
    val chatId: String,
    val senderId: String,
    val contentType: String = "TEXT",
    val textContent: String,
)

data class TokenChunkEvent(
    val chatId: String? = null,
    // Backend uses "content" for token streaming.
    val content: String? = null,
    val rawPayload: String? = null,
)

data class StreamEndEvent(
    val chatId: String? = null,
    val botMessageId: String? = null,
    val rawPayload: String? = null,
)

data class ChatActionEvent(
    val action: String? = null,
    val description: String? = null,
    val rawPayload: String? = null,
)

data class SystemMessageEvent(
    val chatId: String? = null,
    val id: String? = null,
    val senderId: String? = null,
    val contentType: String? = null,
    val content: String? = null,
    val rawPayload: String? = null,
)

data class CardsEvent(
    val chatId: String? = null,
    val rawPayload: String? = null,
)

interface AgentSocket {
    val connectionState: StateFlow<SocketConnectionState>
    val errors: SharedFlow<AgentSocketError>
    val joinedChats: SharedFlow<JoinedChatsEvent>
    val tokenChunks: SharedFlow<TokenChunkEvent>
    val streamEnd: SharedFlow<StreamEndEvent>
    val chatActions: SharedFlow<ChatActionEvent>
    val systemMessages: SharedFlow<SystemMessageEvent>
    val cards: SharedFlow<CardsEvent>

    suspend fun connect()
    suspend fun disconnect()

    suspend fun joinActiveChats(userId: String)
    /**
     * Join a specific chat room so the server will stream events (token chunks, system messages, etc.)
     * for that chat to this socket connection.
     *
     * Backend naming observed in Postman/web client: emit `join_chat` before `send_msg`.
     */
    suspend fun joinChat(chatId: String, roomId: String? = null)
    suspend fun sendMsg(payload: SendMsgPayload)
}
