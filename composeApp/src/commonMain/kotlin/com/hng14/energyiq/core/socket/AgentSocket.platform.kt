package com.hng14.energyiq.core.socket

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Fallback implementation for non-Android targets (no sockets wired yet).
 */
class UnsupportedAgentSocket : AgentSocket {
    private val _state = MutableStateFlow<SocketConnectionState>(SocketConnectionState.Disconnected)
    override val connectionState: StateFlow<SocketConnectionState> = _state

    private val _errors = MutableSharedFlow<AgentSocketError>(extraBufferCapacity = 16)
    override val errors: SharedFlow<AgentSocketError> = _errors

    private val _joinedChats = MutableSharedFlow<JoinedChatsEvent>(extraBufferCapacity = 16)
    override val joinedChats: SharedFlow<JoinedChatsEvent> = _joinedChats

    private val _tokenChunks = MutableSharedFlow<TokenChunkEvent>(extraBufferCapacity = 16)
    override val tokenChunks: SharedFlow<TokenChunkEvent> = _tokenChunks

    private val _streamEnd = MutableSharedFlow<StreamEndEvent>(extraBufferCapacity = 16)
    override val streamEnd: SharedFlow<StreamEndEvent> = _streamEnd

    private val _chatActions = MutableSharedFlow<ChatActionEvent>(extraBufferCapacity = 16)
    override val chatActions: SharedFlow<ChatActionEvent> = _chatActions

    private val _systemMessages = MutableSharedFlow<SystemMessageEvent>(extraBufferCapacity = 16)
    override val systemMessages: SharedFlow<SystemMessageEvent> = _systemMessages

    private val _cards = MutableSharedFlow<CardsEvent>(extraBufferCapacity = 16)
    override val cards: SharedFlow<CardsEvent> = _cards

    override suspend fun connect() {
        _errors.tryEmit(AgentSocketError(code = "UNSUPPORTED", message = "Agent socket is only implemented on Android."))
    }

    override suspend fun disconnect() = Unit

    override suspend fun joinActiveChats(userId: String) {
        _errors.tryEmit(AgentSocketError(code = "UNSUPPORTED", message = "Agent socket is only implemented on Android."))
    }

    override suspend fun joinChat(chatId: String, roomId: String?) {
        _errors.tryEmit(AgentSocketError(code = "UNSUPPORTED", message = "Agent socket is only implemented on Android."))
    }

    override suspend fun sendMsg(payload: SendMsgPayload) {
        _errors.tryEmit(AgentSocketError(code = "UNSUPPORTED", message = "Agent socket is only implemented on Android."))
    }
}
