package com.hng14.energyiq.features.chat.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.*
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.chat.presentation.components.*
import com.hng14.energyiq.features.home.presentation.components.BackHomeTopBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ChatListScreen(
    onOpenConversation: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<ChatListViewModel>()
    val state by viewModel.state.collectAsState()
    val auth = koinInject<AuthRepository>()
    var userName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        userName = auth.getCurrentUser()?.name
    }
    // When navigating back to this screen, force a refresh so newly-created chats appear immediately.
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    val dmSans = dmSansFontFamily()
    var activeMenuConversationId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    var showNotificationsComingSoon by remember { mutableStateOf(false) }
    val categories = listOf(
        ChatCategory.ALL to "All",
        ChatCategory.SOLAR to "Solar",
        ChatCategory.ALERTS to "Alerts",
        ChatCategory.REPORTS to "Reports",
    )

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Color.White,
        topBar = {
            BackHomeTopBar(
                title = "Chats",
                name = userName,
                onBack = onBack,
                onNotificationClick = { showNotificationsComingSoon = true },
                onProfileClick = onOpenProfile,
            )
        },
    ) { paddingValues ->
        if (showNotificationsComingSoon) {
            ComingSoonDialog(
                featureName = "Notifications",
                onDismiss = { showNotificationsComingSoon = false },
            )
        }
        if (state.isLoading && state.conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFFF59E0B))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    categories.forEach { (category, label) ->
                        CategoryChip(
                            label = label,
                            selected = state.selectedCategory == category,
                            onClick = { viewModel.onCategorySelected(category) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Refresh chats from the REST API.
                    SmallActionButton {
                        TransactionHistoryIcon(
                            contentDescription = "Refresh",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.refresh() },
                            tint = Color(0xFF6B7280),
                        )
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF172033),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onNewChat)
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "New Chat",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                ),
                                color = Color.White,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))

                val filtered = viewModel.filteredConversations
                if (!state.isLoading && filtered.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFC),
                        tonalElevation = 0.dp,
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "No chats yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    lineHeight = 18.sp,
                                ),
                                color = Color(0xFF0F172A),
                            )
                            Text(
                                text = "Start a new conversation to get help with your solar, battery, and inverter insights.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp,
                                ),
                                color = Color(0xFF475569),
                            )
                        }
                    }
                } else {
                    ChatSection.entries.forEach { section ->
                        val sectionItems = filtered.filter { it.section == section }
                        if (sectionItems.isNotEmpty()) {
                            ConversationSection(
                                title = when (section) {
                                    ChatSection.TODAY -> "Today"
                                    ChatSection.YESTERDAY -> "Yesterday"
                                    ChatSection.THIS_WEEK -> "This Week"
                                },
                                items = sectionItems,
                                onOpenConversation = onOpenConversation,
                                activeMenuConversationId = activeMenuConversationId,
                                onMenuConversationChange = { activeMenuConversationId = it },
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }
        }
    }
}
