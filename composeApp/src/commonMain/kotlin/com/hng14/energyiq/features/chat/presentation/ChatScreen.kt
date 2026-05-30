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
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.home.presentation.components.BackHomeTopBar
import com.hng14.energyiq.core.ui.ComingSoonDialog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.compose.koinInject
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.random.Random

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    conversationId: String? = null,
    onOpenProfile: () -> Unit,
) {
    // Generate a unique session key for new chats so that navigating back and starting
    // a "New Chat" again doesn't reuse the same ViewModel instance (which might have stale history).
    val viewModelKey = rememberSaveable(conversationId) {
        if (conversationId != null) "ChatViewModel:$conversationId"
        else "ChatViewModel:new:${Random.nextInt()}"
    }

    val viewModel = koinViewModel<ChatViewModel>(
        key = viewModelKey,
    ) { parametersOf(conversationId) }
    val state by viewModel.state.collectAsState()
    val authRepository = koinInject<AuthRepository>()

    var userInitials by remember { mutableStateOf("ME") }
    var userName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val user = authRepository.getCurrentUser()
        val seed = (user?.name?.takeIf { it.isNotBlank() } ?: user?.email ?: "").trim()
        userName = user?.name?.takeIf { it.isNotBlank() } ?: user?.email
        userInitials = initialsFrom(seed).ifBlank { "ME" }
    }
    
    val scrollState = rememberScrollState()
    var showNotificationsComingSoon by remember { mutableStateOf(false) }
    val attachmentController = rememberChatAttachmentController { newAttachments ->
        viewModel.onAddAttachments(newAttachments)
    }
    val hasConversationStarted = state.messages.isNotEmpty()
    val dividerLabel = remember(state.messages) { computeDayDividerLabel(state.messages) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding(),
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
            if (showNotificationsComingSoon) {
                ComingSoonDialog(
                    featureName = "Notifications",
                    onDismiss = { showNotificationsComingSoon = false },
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                ) {
                    BackHomeTopBar(
                        title = state.conversationMeta.title.ifBlank { "Conversation" },
                        name = userName,
                        onBack = onBack,
                        onNotificationClick = { showNotificationsComingSoon = true },
                        onProfileClick = onOpenProfile,
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ChatHeader(
                            onBack = onBack,
                            title = "",
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
                            showBack = false,
                        )
                        state.socketError?.let { msg ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = msg,
                                color = Color(0xFFD92D20),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        if (state.isLoadingHistory) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = Color(0xFFF59E0B))
                            }
                        } else if (hasConversationStarted) {
                            Spacer(modifier = Modifier.height(24.dp))
                            ConversationStartedContent(
                                messages = state.messages,
                                isAgentTyping = state.isAgentTyping,
                                userInitials = userInitials,
                                dividerLabel = dividerLabel,
                            )
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
    isAgentTyping: Boolean,
    userInitials: String,
    dividerLabel: String,
) {
    val conversationScrollState = rememberScrollState()

    LaunchedEffect(messages) {
        conversationScrollState.animateScrollTo(conversationScrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
    ) {
        TodayDivider(label = dividerLabel)
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
                isAgentTyping = isAgentTyping,
                userInitials = userInitials,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

private fun initialsFrom(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return ""

    val parts = trimmed.split(" ").filter { it.isNotBlank() }
    val letters = when {
        parts.size >= 2 -> "" + parts.first().first() + parts.last().first()
        else -> trimmed.take(2)
    }
    return letters.uppercase()
}

private fun computeDayDividerLabel(messages: List<ChatMessage>): String {
    val iso = messages.firstOrNull { !it.createdAtIso.isNullOrBlank() }?.createdAtIso ?: return "Today"
    val instant = runCatching { Instant.parse(iso) }.getOrNull() ?: return "Today"

    val tz = TimeZone.currentSystemDefault()
    val msgDate = instant.toLocalDateTime(tz).date
    val today = Clock.System.now().toLocalDateTime(tz).date
    return when (msgDate) {
        today -> "Today"
        today.minus(1, kotlinx.datetime.DateTimeUnit.DAY) -> "Yesterday"
        else -> "${msgDate.dayOfMonth} ${msgDate.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)}"
    }
}
