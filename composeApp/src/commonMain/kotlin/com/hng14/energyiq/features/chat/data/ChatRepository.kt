package com.hng14.energyiq.features.chat.data

import androidx.compose.ui.graphics.Color
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.chat.data.remote.ChatApi
import com.hng14.energyiq.features.chat.data.remote.dto.ChatMessageDto
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class ChatRepository(
    private val api: ChatApi,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    val conversations = listOf(
        ChatConversationSummary(
            id = "solar-output-drop-unit-3",
            title = "Solar Output Drop - Unit 3",
            subtitle = "Identified a 28% drop vs forecast.\nLikely inverter throttling",
            tag = "Solar",
            tagColor = Color(0xFF16A34A),
            section = ChatSection.TODAY,
            category = ChatCategory.SOLAR,
            timestamp = "Today, 6:10am",
            icon = ChatConversationIcon.DANGER,
        ),
        ChatConversationSummary(
            id = "battery-critically-low-3",
            title = "Battery Critically Low - 3%",
            subtitle = "Battery at 3% - recommend load\nshedding immediately",
            tag = "Alert",
            tagColor = Color(0xFFDC2626),
            section = ChatSection.TODAY,
            category = ChatCategory.ALERTS,
            timestamp = "Today, 7:40am",
            icon = ChatConversationIcon.BATTERY_CHARGING,
        ),
        ChatConversationSummary(
            id = "weekly-report-28-apr",
            title = "Weekly Report - 28 Apr",
            subtitle = "Report generated and sent to 3 recipients",
            tag = "Report",
            tagColor = Color(0xFF6B7280),
            section = ChatSection.YESTERDAY,
            category = ChatCategory.REPORTS,
            timestamp = "Yesterday, 6:10pm",
            icon = ChatConversationIcon.DANGER,
        ),
        ChatConversationSummary(
            id = "battery-critically-low-5",
            title = "Battery Critically Low - 5%",
            subtitle = "Battery at 5% - recommend load shedding\nimmediately",
            tag = "Alert",
            tagColor = Color(0xFFDC2626),
            section = ChatSection.THIS_WEEK,
            category = ChatCategory.ALERTS,
            timestamp = "2 May",
            icon = ChatConversationIcon.DANGER,
        ),
        ChatConversationSummary(
            id = "battery-critically-low-3-older",
            title = "Battery Critically Low - 3%",
            subtitle = "Battery at 3% - recommend load shedding\nimmediately",
            tag = "Alert",
            tagColor = Color(0xFFDC2626),
            section = ChatSection.THIS_WEEK,
            category = ChatCategory.ALERTS,
            timestamp = "4 May",
            icon = ChatConversationIcon.DANGER,
        ),
    )

    fun findConversation(conversationId: String): ChatConversationSummary? =
        conversations.firstOrNull { it.id == conversationId }

    fun buildSeededConversation(conversationId: String): SeededConversation? {
        val summary = findConversation(conversationId) ?: return null
        return when (conversationId) {
            "battery-critically-low-3" -> SeededConversation(
                meta = previewConversationMeta(),
                messages = buildConversationPreview("What triggered the critical battery alert?"),
            )
            else -> SeededConversation(
                meta = ConversationMeta(
                    title = summary.title,
                    subtitle = summary.timestamp,
                ),
                messages = listOf(
                    ChatMessage(
                        id = "$conversationId-user",
                        text = summary.title,
                        isUser = true,
                        timestamp = "9:14am",
                    ),
                    ChatMessage(
                        id = "$conversationId-bot",
                        text = summary.subtitle.replace("\n", " "),
                        isUser = false,
                        timestamp = "9:15am",
                        kind = ChatMessageKind.PLAIN,
                    ),
                ),
            )
        }
    }

    fun previewConversationMeta(): ConversationMeta = ConversationMeta(
        title = "Battery critically low",
        subtitle = "Today, 6:10 am . 5 messages",
    )

    fun buildConversationPreview(userPrompt: String): List<ChatMessage> = listOf(
        ChatMessage(
            id = "message-user",
            text = userPrompt,
            isUser = true,
            timestamp = "9:14am",
        ),
        ChatMessage(
            id = "message-bot-summary",
            text = "Triggered: Today 6:08AM | Status: Unresolved\n\nOvernight grid draw: 47% above baseline. HVAC ran outside schedule.",
            isUser = false,
            timestamp = "9:15am",
            kind = ChatMessageKind.ALERT_SUMMARY,
        ),
        ChatMessage(
            id = "message-bot-followup",
            text = "Battery level dropped to 3% at 6:08 AM. Overnight grid draw was 47% above baseline — analysis points to HVAC running outside its scheduled window (11 PM–5 AM).",
            isUser = false,
            timestamp = "9:15am",
            kind = ChatMessageKind.FOLLOW_UP,
        ),
    )

    fun nextPreviewTimestamp(index: Int): String = when (index) {
        0 -> "9:14am"
        1, 2 -> "9:15am"
        else -> "9:${15 + (index - 2)}am"
    }

    suspend fun createChat(startingMessage: String? = null): com.hng14.energyiq.features.chat.data.remote.dto.ChatDto {
        // Returns server chat object.
        return api.createChat(startingMessage = startingMessage).data
    }

    suspend fun getChats(): List<ChatConversationSummary> {
        val res = api.getChats()
        if (!res.success) return emptyList()

        // Ensure newest chats appear first regardless of backend ordering.
        val sorted = res.data.sortedByDescending { dto ->
            val iso = dto.lastMessageTimestamp ?: dto.updatedAt ?: dto.createdAt ?: ""
            epochMillisOrZero(iso)
        }

        return sorted.map { dto ->
            val iso = dto.lastMessageTimestamp
                ?: dto.updatedAt
                ?: dto.createdAt
                ?: ""
            val section = sectionFromIso(iso)
            val sanitizedPreview = sanitizeChatPreview(
                dto.title,
                dto.lastMessagePreview,
            )
            ChatConversationSummary(
                id = dto.id,
                title = sanitizedPreview.ifBlank { "Untitled chat" },
                // Screenshot UI doesn't show a subtitle; keep it empty unless we have a distinct preview.
                subtitle = "",
                tag = "Alert",
                tagColor = Color(0xFFDC2626),
                section = section,
                category = ChatCategory.ALERTS,
                timestamp = formatTimestamp(iso),
                icon = ChatConversationIcon.BATTERY_CHARGING,
            )
        }
    }

    private fun epochMillisOrZero(iso: String): Long {
        val instant = parseInstant(iso) ?: return 0L
        // kotlin.time.Instant doesn't expose toEpochMilliseconds() on all targets.
        return (instant.epochSeconds * 1000L) + (instant.nanosecondsOfSecond / 1_000_000L)
    }

    fun sanitizeTitle(title: String?): String {
        val t = title?.trim().orEmpty()
        if (t.isBlank()) return ""
        // If it starts with a markdown code block or contains JSON keys, it's a technical payload, not a title.
        if (t.startsWith("```") || t.contains("\"cards\"") || t.contains("\"title\"")) return ""
        return t
    }

    private fun sanitizeChatPreview(
        title: String?,
        lastMessagePreview: String?,
    ): String {
        val sanitizedTitle = sanitizeTitle(title)
        if (sanitizedTitle.isNotBlank()) return sanitizedTitle

        val p = lastMessagePreview?.trim().orEmpty()
        if (p.isBlank()) return ""

        // Avoid showing raw card payloads (often JSON or ```json ... ```).
        val lower = p.lowercase()
        val looksLikeCardsPayload =
            lower.contains("\"cards\"") &&
                (p.startsWith("{") || p.startsWith("```") || lower.contains("{"))
        if (looksLikeCardsPayload) return ""

        return p
    }

    suspend fun getChatMessages(
        chatId: String,
        userId: String,
    ): List<ChatMessage> {
        val res = api.getChatMessages(chatId = chatId, userId = userId)
        if (!res.success) return emptyList()

        val dtos: List<ChatMessageDto> =
            res.messages
                ?: runCatching {
                    when (val d = res.data) {
                        is JsonArray -> d.mapNotNull { el -> json.decodeFromJsonElement(ChatMessageDto.serializer(), el) }
                        is JsonObject -> {
                            val arr = d["messages"] ?: d["data"] ?: return@runCatching emptyList()
                            arr.jsonArray.mapNotNull { el -> json.decodeFromJsonElement(ChatMessageDto.serializer(), el) }
                        }
                        else -> emptyList()
                    }
                }.getOrDefault(emptyList())

        return dtos.map { dto ->
            val iso = dto.createdAt ?: dto.updatedAt ?: ""
            val kind = ChatMessageKind.PLAIN
            val finalText = dto.content
            ChatMessage(
                id = dto.id,
                text = finalText,
                isUser = dto.senderId == userId,
                timestamp = formatMessageTime(iso),
                kind = kind,
                createdAtIso = iso.ifBlank { null },
            )
        }
    }

    private fun sectionFromIso(iso: String): ChatSection {
        val dt = parseInstant(iso)?.toLocalDateTime(TimeZone.currentSystemDefault()) ?: return ChatSection.TODAY
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return when (dt.date) {
            today -> ChatSection.TODAY
            today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> ChatSection.YESTERDAY
            else -> ChatSection.THIS_WEEK
        }
    }

    private fun formatTimestamp(iso: String): String {
        val instant = parseInstant(iso) ?: return ""
        val tz = TimeZone.currentSystemDefault()
        val dt = instant.toLocalDateTime(tz)
        val nowDate = Clock.System.now().toLocalDateTime(tz).date
        val dateLabel = when (dt.date) {
            nowDate -> "Today"
            nowDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> "Yesterday"
            else -> dt.date.toString()
        }

        val hour = dt.hour
        val minute = dt.minute
        val amPm = if (hour < 12) "am" else "pm"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minPadded = minute.toString().padStart(2, '0')
        return if (dateLabel == "Today" || dateLabel == "Yesterday") {
            "$dateLabel, $displayHour:$minPadded$amPm"
        } else {
            "$dateLabel, $displayHour:$minPadded$amPm"
        }
    }

    private fun parseInstant(iso: String): Instant? {
        if (iso.isBlank()) return null
        return runCatching { Instant.parse(iso) }.getOrNull()
    }

    private fun formatMessageTime(iso: String): String {
        val instant = parseInstant(iso) ?: return ""
        val tz = TimeZone.currentSystemDefault()
        val dt = instant.toLocalDateTime(tz)
        val hour = dt.hour
        val minute = dt.minute
        val amPm = if (hour < 12) "am" else "pm"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minPadded = minute.toString().padStart(2, '0')
        return "$displayHour:$minPadded$amPm"
    }

}
