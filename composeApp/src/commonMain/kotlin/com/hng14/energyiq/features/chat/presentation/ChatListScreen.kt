package com.hng14.energyiq.features.chat.presentation

import androidx.compose.foundation.clickable
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
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.chat.presentation.components.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListScreen(
    onOpenConversation: (String) -> Unit,
    onNewChat: () -> Unit,
) {
    val viewModel = koinViewModel<ChatListViewModel>()
    val state by viewModel.state.collectAsState()
    
    val dmSans = dmSansFontFamily()
    var activeMenuConversationId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
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
            ChatListTopBar()
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            categories.forEach { (category, label) ->
                CategoryPill(
                    label = label,
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmallActionButton {
                    TransactionHistoryIcon(
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF6B7280),
                    )
                }
                SmallActionButton {
                    DownloadIcon(
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
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

            ChatSection.entries.forEach { section ->
                val sectionItems = viewModel.filteredConversations.filter { it.section == section }
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
