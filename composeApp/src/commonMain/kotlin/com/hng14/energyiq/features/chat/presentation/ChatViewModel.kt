package com.hng14.energyiq.features.chat.presentation

import androidx.lifecycle.ViewModel
import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChatState(
    val prompt: String = "",
    val attachments: List<ChatAttachment> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val conversationMeta: ConversationMeta = ConversationMeta(
        title = "New Conversation",
        subtitle = "Your Energy Intelligence\nAssistant",
    ),
    val isConversationMenuExpanded: Boolean = false,
    val isShareDialogVisible: Boolean = false
)

class ChatViewModel(
    private val repository: ChatRepository,
    private val conversationId: String? = null
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        conversationId?.let { id ->
            repository.buildSeededConversation(id)?.let { seeded ->
                _state.update { it.copy(
                    messages = seeded.messages,
                    conversationMeta = seeded.meta
                ) }
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

        if (currentState.messages.isEmpty()) {
            _state.update { it.copy(
                messages = repository.buildConversationPreview(messageText),
                conversationMeta = repository.previewConversationMeta(),
                prompt = "",
                attachments = emptyList()
            ) }
        } else {
            _state.update { it.copy(
                messages = it.messages + ChatMessage(
                    id = "message-user-${it.messages.size}",
                    text = messageText,
                    isUser = true,
                    timestamp = repository.nextPreviewTimestamp(it.messages.size),
                ),
                prompt = "",
                attachments = emptyList()
            ) }
        }
    }

    fun onConversationMenuExpandedChange(expanded: Boolean) {
        _state.update { it.copy(isConversationMenuExpanded = expanded) }
    }

    fun onShareDialogVisibleChange(visible: Boolean) {
        _state.update { it.copy(isShareDialogVisible = visible) }
    }
}
