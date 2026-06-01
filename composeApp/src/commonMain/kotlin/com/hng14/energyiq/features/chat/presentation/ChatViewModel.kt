package com.hng14.energyiq.features.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.core.socket.AgentSocket
import com.hng14.energyiq.core.socket.SendMsgPayload
import com.hng14.energyiq.core.socket.SocketConnectionState
import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


data class ChatState(
    val prompt: String = "",
    val attachments: List<ChatAttachment> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val conversationMeta: ConversationMeta = ConversationMeta(
        title = "New Conversation",
        subtitle = "Your Energy Intelligence\nAssistant",
    ),
    val chatId: String? = null,
    val socketRoomId: String? = null,
    val socketState: SocketConnectionState = SocketConnectionState.Disconnected,
    val socketError: String? = null,
    // While the agent streams tokens, we keep updating this message.
    val streamingMessageId: String? = null,
    val pendingBotMessageId: String? = null,
    val isAgentTyping: Boolean = false,
    val isLoadingHistory: Boolean = false,
    val isConversationMenuExpanded: Boolean = false,
    val isShareDialogVisible: Boolean = false
)

class ChatViewModel(
    private val repository: ChatRepository,
    private val agentSocket: AgentSocket,
    private val authPreferences: AuthPreferences,
    private val conversationId: String? = null
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    init {
        // Connect as soon as chat screen opens.
        viewModelScope.launch {
            agentSocket.connect()
        }

        viewModelScope.launch {
            agentSocket.connectionState.collect { s ->
                _state.update { it.copy(socketState = s) }
                if (s is SocketConnectionState.Connected) {
                    val userId = authPreferences.getUserId()?.trim().orEmpty()
                    if (userId.isNotBlank()) {
                        agentSocket.joinActiveChats(userId)
                    }
                }
            }
        }

        viewModelScope.launch {
            agentSocket.errors.collect { err ->
                _state.update { it.copy(socketError = "${err.code}: ${err.message}", isAgentTyping = false) }
            }
        }

        viewModelScope.launch {
            agentSocket.tokenChunks.collect { chunk ->
                val currentChatId = _state.value.chatId
                val currentRoomId = _state.value.socketRoomId
                val evtChatId = chunk.chatId

                // Accept if it matches either the DB ID or the Room ID.
                if (currentChatId != null && evtChatId != null && 
                    evtChatId != currentChatId && evtChatId != currentRoomId) return@collect

                val token = chunk.content ?: return@collect
                _state.update { st ->
                    val currentId = st.streamingMessageId ?: "message-agent-stream-${st.messages.size}"

                    // If streamingMessageId is set but the message doesn't exist (rare race),
                    // fall back to creating it so we don't lose chunks.
                    val updated = if (st.streamingMessageId == null || st.messages.none { it.id == currentId }) {
                        // Start a new streaming assistant message.
                        st.messages + ChatMessage(
                            id = currentId,
                            text = token,
                            isUser = false,
                            timestamp = repository.nextPreviewTimestamp(st.messages.size),
                        )
                    } else {
                        st.messages.map { m ->
                            if (m.id == currentId) m.copy(text = m.text + token) else m
                        }
                    }

                    st.copy(messages = updated, streamingMessageId = currentId, isAgentTyping = true)
                }
            }
        }

        viewModelScope.launch {
            agentSocket.streamEnd.collect { end ->
                val currentChatId = _state.value.chatId
                val currentRoomId = _state.value.socketRoomId
                val evtChatId = end.chatId

                if (currentChatId != null && evtChatId != null &&
                    evtChatId != currentChatId && evtChatId != currentRoomId) return@collect

                _state.update {
                    it.copy(
                        streamingMessageId = null,
                        // Some backends never send a "final" system message; they stream tokens and then
                        // end with `stream_end`. If we keep a pendingBotMessageId here, it can block the
                        // next assistant response from rendering. Treat stream_end as finalization.
                        pendingBotMessageId = null,
                        // Backend signals completion here; don't keep the typing bubble hanging around
                        // while we wait for the final `new_system_message` payload.
                        isAgentTyping = false,
                    )
                }
            }
        }

        viewModelScope.launch {
            agentSocket.chatActions.collect { action ->
                val currentChatId = _state.value.chatId
                // chat_action doesn't consistently include chatId in a structured field; don't filter by id here.
                // We rely on systemMessages/tokenChunks/streamEnd (which do carry chatId) to scope updates.
                if (action.action == "typing") {
                    _state.update { it.copy(isAgentTyping = true) }
                }
            }
        }

        // Dedicated cards stream (backend emits event name "cards").
        viewModelScope.launch {
            agentSocket.cards.collect { evt ->
                val currentChatId = _state.value.chatId?.trim().orEmpty()
                val currentRoomId = _state.value.socketRoomId?.trim().orEmpty()
                val evtChatId = evt.chatId?.trim().orEmpty()
                
                if (currentChatId.isNotBlank() && evtChatId.isNotBlank() && 
                    evtChatId != currentChatId && evtChatId != currentRoomId) return@collect

                val cards = parseCardsFromRawPayload(evt.rawPayload)
                if (cards.isEmpty()) return@collect

                _state.update { st ->
                    st.copy(
                        messages = st.messages + ChatMessage(
                            id = "message-cards-${st.messages.size}",
                            text = "",
                            isUser = false,
                            timestamp = repository.nextPreviewTimestamp(st.messages.size),
                            kind = ChatMessageKind.CARDS,
                            cards = cards,
                        ),
                        isAgentTyping = false,
                    )
                }
            }
        }

        viewModelScope.launch {
            agentSocket.systemMessages.collect { msg ->
                val currentChatId = _state.value.chatId
                val currentRoomId = _state.value.socketRoomId
                val evtChatId = msg.chatId

                if (currentChatId != null && evtChatId != null && 
                    evtChatId != currentChatId && evtChatId != currentRoomId) return@collect

                val content = msg.content?.trim().orEmpty()
                if (content.isBlank() && msg.rawPayload.isNullOrBlank()) return@collect

                _state.update { st ->
                    val id = msg.id ?: st.pendingBotMessageId ?: st.streamingMessageId ?: "message-agent-${st.messages.size}"
                    val kind = ChatMessageKind.PLAIN
                    val finalText = content

                    // If we already have a streamed message, replace it with the final system message.
                    val replaced = when {
                        st.streamingMessageId != null -> st.messages.map { m ->
                            if (m.id == st.streamingMessageId) m.copy(id = id, text = finalText, kind = kind) else m
                        }
                        else -> {
                            val exists = st.messages.any { it.id == id }
                            if (exists) {
                                st.messages.map { m -> if (m.id == id) m.copy(text = finalText, kind = kind) else m }
                            } else {
                                st.messages + ChatMessage(
                                    id = id,
                                    text = finalText,
                                    isUser = false,
                                    timestamp = repository.nextPreviewTimestamp(st.messages.size),
                                    kind = kind,
                                )
                            }
                        }
                    }

                    st.copy(
                        messages = replaced,
                        pendingBotMessageId = null,
                        streamingMessageId = null,
                        isAgentTyping = false,
                    )
                }
            }
        }

        conversationId?.let { id ->
            // Load conversation history from REST (proxy) when opening an existing chat.
            viewModelScope.launch {
                val userId = authPreferences.getUserId()?.trim().orEmpty()
                if (userId.isBlank()) return@launch

                _state.update { it.copy(chatId = id, isLoadingHistory = true) }
                println("Chat: loading history chatId=$id userId=$userId")
                
                // Fetch the list of chats to find the title for this specific chat
                viewModelScope.launch {
                    runCatching { repository.getChats() }.onSuccess { chats ->
                        chats.find { it.id == id }?.let { summary ->
                            _state.update { st ->
                                st.copy(
                                    conversationMeta = st.conversationMeta.copy(
                                        title = summary.title,
                                        subtitle = summary.timestamp
                                    )
                                )
                            }
                        }
                    }
                }

                runCatching { repository.getChatMessages(chatId = id, userId = userId) }
                    .onSuccess { msgs ->
                        println("Chat: history loaded count=${msgs.size}")
                        _state.update { st ->
                            st.copy(
                                messages = msgs,
                                isLoadingHistory = false,
                                socketError = null,
                            )
                        }
                    }
                    .onFailure { e ->
                        println("Chat: history load failed error=${e.message}")
                        // Fallback to seeded content if available (dev/demo), but don't block UI.
                        val seeded = repository.buildSeededConversation(id)
                        _state.update { st ->
                            st.copy(
                                messages = seeded?.messages ?: st.messages,
                                conversationMeta = seeded?.meta ?: st.conversationMeta,
                                isLoadingHistory = false,
                                socketError = e.message ?: "Failed to load chat messages",
                            )
                        }
                    }
            }
        }
    }

    fun onPromptChange(newPrompt: String) {
        _state.update { it.copy(prompt = newPrompt) }
    }

    fun onAddAttachments(newAttachments: List<ChatAttachment>) {
        _state.update { 
            it.copy(attachments = (it.attachments + newAttachments).distinctBy { a -> a.uri ?: a.name })
        }
    }

    fun onRemoveAttachment(attachment: ChatAttachment) {
        _state.update { it.copy(attachments = it.attachments.filterNot { a -> a.id == attachment.id }) }
    }

    fun onSend() {
        val currentState = _state.value
        val trimmedPrompt = currentState.prompt.trim()
        if (trimmedPrompt.isBlank() && currentState.attachments.isEmpty()) {
            return
        }

        val attachmentSummary = currentState.attachments
            .takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "\n") { attachment ->
                "Attached: ${attachment.name}"
            }
            .orEmpty()

        val messageText = listOf(trimmedPrompt, attachmentSummary)
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n")

        println(
            "Chat: onSend() chatId=${currentState.chatId.orEmpty()} roomId=${currentState.socketRoomId.orEmpty()} " +
                "socket=${currentState.socketState} promptLen=${trimmedPrompt.length} attachments=${currentState.attachments.size}"
        )

        // Optimistically add only the user message.
        // (We used to inject a demo bot response for the first message; that was confusing once real AI replies exist.)
        val nextMessages = currentState.messages + ChatMessage(
            id = "message-user-${currentState.messages.size}",
            text = messageText,
            isUser = true,
            timestamp = repository.nextPreviewTimestamp(currentState.messages.size),
        )
        // Show typing indicator immediately; we'll clear it when a system message arrives or on error.
        _state.update {
            it.copy(
                messages = nextMessages,
                prompt = "",
                attachments = emptyList(),
                isAgentTyping = true,
                // If a previous stream ended without a final system message, don't block the next response.
                pendingBotMessageId = null,
                streamingMessageId = null,
            )
        }

        // For the first message, we must create a chat via REST to obtain a chatId.
        // We need a chatId before we can `join_chat`/`send_msg` over sockets.
        // Backend currently requires a non-empty `startingMessage`, so we create the chat with the user's
        // first message, then immediately `join_chat` and `send_msg` with the same message.
        //
        // Note: This can lead to duplicate processing if the backend also processes `startingMessage`.
        // Backend should handle idempotency/deduplication if needed.
        viewModelScope.launch {
            val senderId = authPreferences.getUserId()?.trim().orEmpty()
            if (senderId.isBlank()) return@launch

            val existingChatId = _state.value.chatId
            if (existingChatId.isNullOrBlank()) {
                runCatching {
                    repository.createChat(startingMessage = messageText)
                }.onSuccess { chatDto ->
                    println("Chat: created chatId=${chatDto.id} roomId=${chatDto.roomId}")
                    val sanitizedTitle = repository.sanitizeTitle(chatDto.title)
                    _state.update { st ->
                        st.copy(
                            chatId = chatDto.id,
                            socketRoomId = chatDto.roomId,
                            conversationMeta = st.conversationMeta.copy(
                                title = sanitizedTitle.ifBlank { st.conversationMeta.title }
                            )
                        )
                    }
                    // Subscribe to this specific chat immediately, then send the first message.
                    agentSocket.joinChat(chatId = chatDto.id, roomId = chatDto.roomId)
                    agentSocket.sendMsg(
                        SendMsgPayload(
                            chatId = chatDto.id,
                            senderId = senderId,
                            contentType = "TEXT",
                            textContent = messageText,
                        ),
                    )
                }.onFailure { e ->
                    _state.update { it.copy(socketError = e.message ?: "Failed to create chat", isAgentTyping = false) }
                }
                return@launch
            }

            // Defensive: if the socket reconnects mid-session, we may lose room membership.
            // Re-join before sending to ensure we receive token/system events for this chat.
            agentSocket.joinChat(chatId = existingChatId, roomId = _state.value.socketRoomId)
            agentSocket.sendMsg(
                SendMsgPayload(
                    chatId = existingChatId,
                    senderId = senderId,
                    contentType = "TEXT",
                    textContent = messageText,
                ),
            )
        }
    }

    fun onConversationMenuExpandedChange(expanded: Boolean) {
        _state.update { it.copy(isConversationMenuExpanded = expanded) }
    }

    fun onShareDialogVisibleChange(visible: Boolean) {
        _state.update { it.copy(isShareDialogVisible = visible) }
    }

    private fun parseCardsFromRawPayload(rawPayload: String?): List<ChatCard> {
        val raw = rawPayload?.trim().orEmpty()
        if (raw.isBlank() || !raw.contains("\"cards\"", ignoreCase = true)) return emptyList()

        return runCatching {
            val root = json.parseToJsonElement(raw).jsonObject
            val cardsEl = root["cards"] ?: return@runCatching emptyList()
            cardsEl.jsonArray.mapNotNull { el ->
                val obj = el.jsonObject
                val typeRaw = obj["cardType"]?.jsonPrimitive?.content?.trim().orEmpty()
                val title = obj["title"]?.jsonPrimitive?.content?.trim().orEmpty()
                val body = obj["content"]?.jsonPrimitive?.content?.trim().orEmpty()
                if (title.isBlank() && body.isBlank()) return@mapNotNull null

                val type = when (typeRaw.lowercase()) {
                    "summary" -> ChatCardType.SUMMARY
                    "insights", "insight" -> ChatCardType.INSIGHTS
                    "recommendation", "recommendations" -> ChatCardType.RECOMMENDATION
                    "anomaly" -> ChatCardType.ANOMALY
                    else -> ChatCardType.INSIGHTS
                }
                val severityRaw = obj["severity"]?.jsonPrimitive?.content?.trim()
                val severity = when (severityRaw?.lowercase()) {
                    "low" -> ChatCardSeverity.LOW
                    "medium" -> ChatCardSeverity.MEDIUM
                    "high" -> ChatCardSeverity.HIGH
                    "critical" -> ChatCardSeverity.CRITICAL
                    else -> null
                }

                ChatCard(
                    type = type,
                    title = title.ifBlank { "Update" },
                    content = body,
                    severity = severity,
                )
            }
        }.getOrDefault(emptyList())
    }

}
