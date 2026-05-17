package com.hng14.energyiq.features.chat.domain.model

import androidx.compose.ui.graphics.Color

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val kind: ChatMessageKind = ChatMessageKind.PLAIN,
)

enum class ChatMessageKind {
    PLAIN,
    ALERT_SUMMARY,
    FOLLOW_UP,
}

data class ConversationMeta(
    val title: String,
    val subtitle: String,
)

data class SeededConversation(
    val meta: ConversationMeta,
    val messages: List<ChatMessage>,
)

enum class ConversationMenuAction {
    SHARE,
    RENAME,
    PIN,
    ARCHIVE,
    DELETE,
}

enum class ChatCategory {
    ALL,
    SOLAR,
    ALERTS,
    REPORTS,
}

enum class ChatSection {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
}

enum class ChatConversationIcon {
    DANGER,
    BATTERY_CHARGING,
}

data class ChatConversationSummary(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val tagColor: Color,
    val section: ChatSection,
    val category: ChatCategory,
    val timestamp: String,
    val icon: ChatConversationIcon,
)

enum class ChatAttachmentType {
    IMAGE,
    FILE,
    SCREENSHOT,
}

data class ChatAttachment(
    val id: String,
    val name: String,
    val type: ChatAttachmentType,
    val uri: String? = null,
)
