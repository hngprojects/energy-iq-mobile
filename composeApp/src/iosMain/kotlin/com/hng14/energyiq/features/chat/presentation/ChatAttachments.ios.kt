package com.hng14.energyiq.features.chat.presentation

import androidx.compose.runtime.Composable
import com.hng14.energyiq.features.chat.domain.model.ChatAttachment

@Composable
actual fun rememberChatAttachmentController(
    onAttachmentsSelected: (List<ChatAttachment>) -> Unit,
): ChatAttachmentController = ChatAttachmentController()
