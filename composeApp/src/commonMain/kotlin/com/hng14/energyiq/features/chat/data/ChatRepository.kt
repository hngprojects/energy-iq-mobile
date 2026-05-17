package com.hng14.energyiq.features.chat.data

import androidx.compose.ui.graphics.Color
import com.hng14.energyiq.features.chat.domain.model.*

class ChatRepository {
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
}
