package com.hng14.energyiq.features.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.chat.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    conversationId: String? = null,
) {
    val viewModel = koinViewModel<ChatViewModel> { parametersOf(conversationId) }
    val state by viewModel.state.collectAsState()
    
    val scrollState = rememberScrollState()
    val attachmentController = rememberChatAttachmentController { newAttachments ->
        viewModel.onAddAttachments(newAttachments)
    }
    val hasConversationStarted = state.messages.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                ChatComposer(
                    value = state.prompt,
                    attachments = state.attachments,
                    attachmentController = attachmentController,
                    onValueChange = viewModel::onPromptChange,
                    onSend = viewModel::onSend,
                    onRemoveAttachment = viewModel::onRemoveAttachment,
                )
            },
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    ChatTopUtilityBar()
                    Spacer(modifier = Modifier.height(18.dp))
                    ChatHeader(
                        onBack = onBack,
                        title = state.conversationMeta.title,
                        subtitle = state.conversationMeta.subtitle,
                        hasConversationStarted = hasConversationStarted,
                        isConversationMenuExpanded = state.isConversationMenuExpanded,
                        onConversationMenuExpandedChange = viewModel::onConversationMenuExpandedChange,
                        onConversationMenuAction = { action ->
                            when (action) {
                                ConversationMenuAction.SHARE -> viewModel.onShareDialogVisibleChange(true)
                                else -> Unit
                            }
                        },
                    )
                    if (hasConversationStarted) {
                        Spacer(modifier = Modifier.height(24.dp))
                        ConversationStartedContent(messages = state.messages)
                    } else {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                        ) {
                            Spacer(modifier = Modifier.height(35.dp))
                            ChatHero()
                            Spacer(modifier = Modifier.height(32.dp))
                            SuggestedQuestionSection(
                                onSuggestionSelected = { suggestion ->
                                    viewModel.onPromptChange(suggestion)
                                    viewModel.onSend()
                                },
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
                if (state.isConversationMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color(0x66121212))
                            .clickable(
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            ) {
                                viewModel.onConversationMenuExpandedChange(false)
                            },
                    )
                }
            }
            if (state.isShareDialogVisible) {
                ShareChatDialog(
                    onDismiss = { viewModel.onShareDialogVisibleChange(false) },
                )
            }
        }
    }
}

@Composable
private fun ConversationStartedContent(
    messages: List<ChatMessage>,
) {
    val conversationScrollState = rememberScrollState()

    LaunchedEffect(messages.size) {
        conversationScrollState.animateScrollTo(conversationScrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
    ) {
        TodayDivider()
        Spacer(modifier = Modifier.height(18.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(conversationScrollState),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ConversationThread(
                messages = messages,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}
