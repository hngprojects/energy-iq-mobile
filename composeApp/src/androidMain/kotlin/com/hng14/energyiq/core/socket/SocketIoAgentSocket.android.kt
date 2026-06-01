package com.hng14.energyiq.core.socket

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.Ack
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

class SocketIoAgentSocket(
    private val authPreferences: AuthPreferences,
) : AgentSocket {
    private val _connectionState = MutableStateFlow<SocketConnectionState>(SocketConnectionState.Disconnected)
    override val connectionState: StateFlow<SocketConnectionState> = _connectionState

    private val _errors = MutableSharedFlow<AgentSocketError>(extraBufferCapacity = 32)
    override val errors: SharedFlow<AgentSocketError> = _errors

    private val _joinedChats = MutableSharedFlow<JoinedChatsEvent>(extraBufferCapacity = 32)
    override val joinedChats: SharedFlow<JoinedChatsEvent> = _joinedChats

    private val _tokenChunks = MutableSharedFlow<TokenChunkEvent>(extraBufferCapacity = 64)
    override val tokenChunks: SharedFlow<TokenChunkEvent> = _tokenChunks

    private val _streamEnd = MutableSharedFlow<StreamEndEvent>(extraBufferCapacity = 32)
    override val streamEnd: SharedFlow<StreamEndEvent> = _streamEnd

    private val _chatActions = MutableSharedFlow<ChatActionEvent>(extraBufferCapacity = 32)
    override val chatActions: SharedFlow<ChatActionEvent> = _chatActions

    private val _systemMessages = MutableSharedFlow<SystemMessageEvent>(extraBufferCapacity = 32)
    override val systemMessages: SharedFlow<SystemMessageEvent> = _systemMessages

    private val _cards = MutableSharedFlow<CardsEvent>(extraBufferCapacity = 32)
    override val cards: SharedFlow<CardsEvent> = _cards

    private var socket: Socket? = null

    override suspend fun connect() {
        val token = authPreferences.getToken()?.trim().orEmpty()
        if (token.isBlank()) {
            _errors.tryEmit(AgentSocketError(code = "NO_TOKEN", message = "Missing auth token. Please sign in again."))
            return
        }

        // The backend handshake (as observed in Postman) expects `user_id` as a query param.
        // We still emit `join_active_chats` after connect, but sending the user id here makes
        // the server route/session association deterministic.
        val userId = authPreferences.getUserId()?.trim().orEmpty()

        // Recreate socket each time to ensure auth header is always fresh.
        disconnect()

        val baseUrl = SocketUrl.fromApiBaseUrl(NetworkConfig.BASE_URL)
        val namespaceUrl = buildString {
            append(baseUrl)
            append("/agent")
            if (userId.isNotBlank()) {
                append("?user_id=")
                append(userId)
            }
        }

        val opts = IO.Options.builder()
            .setForceNew(true)
            .setReconnection(true)
            // Ngrok + some proxies can be flaky with long-polling; prefer websocket-only.
            .setTransports(arrayOf("websocket"))
            .build()

        @Suppress("UNCHECKED_CAST")
        opts.extraHeaders = mapOf("Authorization" to listOf("Bearer $token"))

        val s = IO.socket(namespaceUrl, opts)
        socket = s

        fun logEvent(event: String, args: Array<out Any?>) {
            // Keep this noisy logging behind println so it is visible in Logcat for debugging.
            val rendered = args.joinToString(prefix = "[", postfix = "]") { it?.toString() ?: "null" }
            println("AgentSocket: incoming event=$event args=$rendered")
        }

        fun emitCardsIfPresent(raw: Any?) {
            val obj = raw as? JSONObject ?: return
            if (!obj.has("cards")) return
            val chatId = obj.optString("chatId").ifBlank {
                obj.optJSONObject("chat")?.optString("id").orEmpty()
            }
            _cards.tryEmit(CardsEvent(chatId = chatId.ifBlank { null }, rawPayload = obj.toString()))
        }

        fun emitSystemMessageIfPresent(raw: Any?) {
            val obj = raw as? JSONObject ?: return
            // If it has cards, it's handled by emitCardsIfPresent.
            if (obj.has("cards")) return

            val content = obj.optString("content").ifBlank {
                obj.optString("text").ifBlank {
                    obj.optString("message").ifBlank { "" }
                }
            }
            if (content.isBlank()) return

            val chatObj = obj.optJSONObject("chat")
            _systemMessages.tryEmit(
                SystemMessageEvent(
                    chatId = chatObj?.optString("id") ?: obj.optString("chatId") ?: obj.optString("id"),
                    id = obj.optString("id"),
                    senderId = obj.optString("senderId"),
                    contentType = obj.optString("contentType", "TEXT"),
                    content = content,
                    rawPayload = obj.toString(),
                )
            )
        }

        s.on(Socket.EVENT_CONNECT) {
            println("AgentSocket: connected url=$namespaceUrl")
            _connectionState.value = SocketConnectionState.Connected
        }
        s.on(Socket.EVENT_DISCONNECT) {
            println("AgentSocket: disconnected")
            _connectionState.value = SocketConnectionState.Disconnected
        }
        s.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val msg = args.firstOrNull()?.toString().orEmpty()
            println("AgentSocket: connect_error=$msg")
            _connectionState.value = SocketConnectionState.Disconnected
            _errors.tryEmit(AgentSocketError(code = "CONNECT_ERROR", message = msg.ifBlank { "Connection error" }))
        }

        // Server documented error event: { code, message }
        s.on("error") { args ->
            logEvent("error", args)
            val raw = args.firstOrNull()
            val obj = raw as? JSONObject
            if (obj != null) {
                _errors.tryEmit(
                    AgentSocketError(
                        code = obj.optString("code", "ERROR"),
                        message = obj.optString("message", "Unknown error"),
                    ),
                )
            } else {
                _errors.tryEmit(AgentSocketError(code = "ERROR", message = raw?.toString().orEmpty()))
            }
        }

        // Debug: log candidate events to discover the real server->client message event names.
        // Important: do NOT include known streaming events here (token_chunk/stream_end/etc),
        // otherwise we may accidentally treat token payloads as "system messages" and duplicate UI output.
        listOf(
            "message",
            "new_msg",
            "msg",
            "receive_msg",
            "chat_message",
            "agent_msg",
            "agent_message",
            "assistant_msg",
            "assistant_message",
            "msg_sent",
            "msg_received",
            "sent_msg",
            "send_msg",
            // Some docs/screenshots refer to join_chats instead of joined_chats
            "join_chats",
        ).forEach { event ->
            s.on(event) { args ->
                logEvent(event, args)
                val firstArg = args.firstOrNull()
                emitCardsIfPresent(firstArg)
                emitSystemMessageIfPresent(firstArg)
            }
        }

        s.on("token_chunk") { args ->
            val raw = args.firstOrNull()
            val obj = raw as? JSONObject
            _tokenChunks.tryEmit(
                TokenChunkEvent(
                    chatId = obj?.optString("chatId") ?: obj?.optString("id") ?: obj?.optString("roomId"),
                    content = obj?.optString("content")
                        ?: obj?.optString("token")
                        ?: obj?.optString("chunk")
                        ?: obj?.optString("text"),
                    rawPayload = raw?.toString(),
                ),
            )
        }

        s.on("stream_end") { args ->
            val raw = args.firstOrNull()
            val obj = raw as? JSONObject
            _streamEnd.tryEmit(
                StreamEndEvent(
                    chatId = obj?.optString("chatId") ?: obj?.optString("id") ?: obj?.optString("roomId"),
                    botMessageId = obj?.optString("botMessageId") ?: obj?.optString("messageId"),
                    rawPayload = raw?.toString(),
                ),
            )
        }

        s.on("chat_action") { args ->
            val raw = args.firstOrNull()
            val obj = raw as? JSONObject
            _chatActions.tryEmit(
                ChatActionEvent(
                    action = obj?.optString("action") ?: obj?.optString("type"),
                    description = obj?.optString("description"),
                    rawPayload = raw?.toString(),
                ),
            )
        }

        s.on("new_system_message") { args ->
            val raw = args.firstOrNull()
            val obj = raw as? JSONObject
            val chatObj = obj?.optJSONObject("chat")
            emitCardsIfPresent(raw)
            _systemMessages.tryEmit(
                SystemMessageEvent(
                    chatId = chatObj?.optString("id") ?: obj?.optString("chatId") ?: obj?.optString("id"),
                    id = obj?.optString("id"),
                    senderId = obj?.optString("senderId"),
                    contentType = obj?.optString("contentType"),
                    content = obj?.optString("content"),
                    rawPayload = raw?.toString(),
                ),
            )
        }

        // Observed event name from web client logs and Postman: "new_system_msg"
        s.on("new_system_msg") { args ->
            val firstArg = args.firstOrNull()
            logEvent("new_system_msg", args)
            emitCardsIfPresent(firstArg)
            emitSystemMessageIfPresent(firstArg)
        }

        s.on("joined_chats") { args ->
            val raw = args.firstOrNull()
            when (raw) {
                is JSONObject -> {
                    _joinedChats.tryEmit(
                        JoinedChatsEvent(
                            userId = raw.optString("userId", ""),
                            field2 = raw.optInt("field2"),
                            field3 = raw.optBoolean("field3"),
                            rawPayload = raw.toString(),
                        ),
                    )
                }

                is JSONArray -> {
                    // Observed from Postman: joined_chats can be an array (likely list of chats).
                    // We auto-join all chats found in the list to ensure we are receiving events.
                    // Important: backend may use either `chat.id` or `chat.roomId` as the socket room key
                    // (Postman screenshots show token events keyed by chatId), so join both defensively.
                    for (i in 0 until raw.length()) {
                        val chat = raw.optJSONObject(i)
                        val chatId = chat?.optString("id").orEmpty().ifBlank { null }
                        val roomId = chat?.optString("roomId").orEmpty().ifBlank { null }

                        val roomKeys = listOfNotNull(chatId, roomId).distinct()
                        for (key in roomKeys) {
                            println("AgentSocket: auto-joining roomKey=$key (chatId=$chatId roomId=$roomId)")
                            // Emit multiple common join patterns just in case.
                            s.emit("join", JSONObject().put("room", key))
                            s.emit("join_room", JSONObject().put("chatId", key))
                            s.emit("join_room", JSONObject().put("roomId", key))
                        }
                    }
                    _joinedChats.tryEmit(
                        JoinedChatsEvent(
                            userId = "",
                            rawPayload = raw.toString(),
                        ),
                    )
                }

                else -> {
                    _joinedChats.tryEmit(JoinedChatsEvent(userId = "", rawPayload = raw?.toString()))
                }
            }
        }

        _connectionState.value = SocketConnectionState.Connecting
        println("AgentSocket: connecting url=$namespaceUrl")
        s.connect()
    }

    override suspend fun disconnect() {
        val s = socket ?: return
        socket = null
        runCatching {
            s.off()
            s.disconnect()
            s.close()
        }
        _connectionState.value = SocketConnectionState.Disconnected
    }

    override suspend fun joinActiveChats(userId: String) {
        val s = socket
        if (s == null || !s.connected()) {
            _errors.tryEmit(AgentSocketError(code = "NOT_CONNECTED", message = "Socket is not connected"))
            return
        }
        val payload = JSONObject().put("userId", userId)
        println("AgentSocket: emit join_active_chats userId=$userId")
        s.emit("join_active_chats", payload)
    }

    override suspend fun joinChat(chatId: String, roomId: String?) {
        val s = socket
        if (s == null || !s.connected()) {
            _errors.tryEmit(AgentSocketError(code = "NOT_CONNECTED", message = "Socket is not connected"))
            return
        }

        // Postman/web flow: emit join_chat then send_msg for that chatId.
        val payload = JSONObject().put("chatId", chatId)
        if (!roomId.isNullOrBlank()) payload.put("roomId", roomId)
        println("AgentSocket: emit join_chat chatId=$chatId roomId=${roomId.orEmpty()}")
        s.emit("join_chat", payload)

        // Some deployments use room-based joins; join both keys defensively.
        val roomKeys = listOfNotNull(chatId.takeIf { it.isNotBlank() }, roomId?.takeIf { it.isNotBlank() }).distinct()
        for (key in roomKeys) {
            s.emit("join", JSONObject().put("room", key))
            s.emit("join_room", JSONObject().put("chatId", key))
            s.emit("join_room", JSONObject().put("roomId", key))
        }
    }

    override suspend fun sendMsg(payload: SendMsgPayload) {
        val s = socket
        if (s == null || !s.connected()) {
            _errors.tryEmit(AgentSocketError(code = "NOT_CONNECTED", message = "Socket is not connected"))
            return
        }
        val obj = JSONObject()
            .put("chatId", payload.chatId)
            .put("senderId", payload.senderId)
            .put("contentType", payload.contentType)
            .put("textContent", payload.textContent)
        println("AgentSocket: emit send_msg chatId=${payload.chatId} senderId=${payload.senderId}")
        s.emit("send_msg", obj, Ack { ackArgs ->
            // If the server acks, this is often where "Invalid namespace"/validation errors show up.
            val rendered = ackArgs.joinToString(prefix = "[", postfix = "]") { it?.toString() ?: "null" }
            println("AgentSocket: ack send_msg args=$rendered")
        })
    }
}
