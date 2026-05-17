package com.hng14.energyiq.features.chat.presentation

import androidx.compose.runtime.Composable
import com.hng14.energyiq.features.chat.domain.model.ChatAttachment

class ChatAttachmentController(
    val pickPhotoOrFiles: () -> Unit = {},
    val takeScreenshot: () -> Unit = {},
)

@Composable
expect fun rememberChatAttachmentController(
    onAttachmentsSelected: (List<ChatAttachment>) -> Unit,
): ChatAttachmentController
