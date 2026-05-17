package com.hng14.energyiq.features.chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.BatteryChargingIcon
import com.hng14.energyiq.core.ui.DangerVectorIcon
import com.hng14.energyiq.features.chat.domain.model.*
import com.hng14.energyiq.features.chat.presentation.ChatAttachmentController

@Composable
fun TodayDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFFE5E7EB)),
        )
        Text(
            text = "Today",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSansFontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 12.sp,
            ),
            color = Color(0xFF8A9099),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFFE5E7EB)),
        )
    }
}

@Composable
fun ChatTopUtilityBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "≡",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4B5563),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = "Notifications",
                tint = Color(0xFF4B5563),
                modifier = Modifier.size(20.dp),
            )
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = Color(0xFFF6D6A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AJ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1F2937),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun ChatHeader(
    onBack: () -> Unit,
    title: String,
    subtitle: String,
    hasConversationStarted: Boolean,
    isConversationMenuExpanded: Boolean,
    onConversationMenuExpandedChange: (Boolean) -> Unit,
    onConversationMenuAction: (ConversationMenuAction) -> Unit,
) {
    val dmSans = dmSansFontFamily()

    Row(verticalAlignment = Alignment.Top) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(28.dp)
                .padding(top = 2.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF4B5563),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            Column(
                modifier = Modifier.clickable { onConversationMenuExpandedChange(true) },
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.2).sp,
                    ),
                    color = Color(0xFF141414),
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontWeight = if (hasConversationStarted) FontWeight.Normal else FontWeight.Medium,
                            fontSize = 12.sp,
                            lineHeight = if (hasConversationStarted) 14.sp else 18.sp,
                        ),
                        color = Color(0xFF666666),
                    )
                }
            }
            ConversationOptionsMenu(
                expanded = isConversationMenuExpanded,
                onDismiss = { onConversationMenuExpandedChange(false) },
                onActionSelected = { action ->
                    onConversationMenuExpandedChange(false)
                    onConversationMenuAction(action)
                },
            )
        }
    }
}

@Composable
fun ConversationOptionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onActionSelected: (ConversationMenuAction) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val items = listOf(
        Triple(ConversationMenuAction.SHARE, "Share", Icons.Outlined.Share),
        Triple(ConversationMenuAction.RENAME, "Rename", Icons.Outlined.Edit),
        Triple(ConversationMenuAction.PIN, "Pin Chat", Icons.Outlined.PushPin),
        Triple(ConversationMenuAction.ARCHIVE, "Archive", Icons.Outlined.Inventory2),
        Triple(ConversationMenuAction.DELETE, "Delete", Icons.Outlined.DeleteOutline),
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(151.dp)
            .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        containerColor = Color.White,
        shadowElevation = 20.dp,
    ) {
        items.forEachIndexed { index, (action, label, icon) ->
            val isDelete = action == ConversationMenuAction.DELETE
            DropdownMenuItem(
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                        ),
                        color = if (isDelete) Color(0xFFDC2626) else Color(0xFF141414),
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDelete) Color(0xFFDC2626) else Color(0xFF222222),
                        modifier = Modifier.size(20.dp),
                    )
                },
                onClick = { onActionSelected(action) },
            )
            if (index != items.lastIndex) {
                HorizontalDivider(
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp,
                )
            }
        }
    }
}

@Composable
fun ShareChatDialog(
    onDismiss: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    var isPublicSelected by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color(0xFF8A9099),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null,
                                    tint = Color(0xFF374151),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Share Chat",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                ),
                                color = Color(0xFF141414),
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Only messages up to this point will be shared",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                ),
                                color = Color(0xFF5D5C5D),
                                modifier = Modifier.width(210.dp),
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close share dialog",
                        tint = Color(0xFF141414),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onDismiss),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                ShareOptionCard(
                    icon = Icons.Outlined.Lock,
                    title = "Keep private",
                    subtitle = "Only you have access",
                    selected = !isPublicSelected,
                    onClick = { isPublicSelected = false },
                )
                Spacer(modifier = Modifier.height(10.dp))
                ShareOptionCard(
                    icon = Icons.Outlined.Language,
                    title = "Create public link",
                    subtitle = "Anyone can the link can view",
                    selected = isPublicSelected,
                    onClick = { isPublicSelected = true },
                )
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "https://energyiqchat/share/ffuu4jt-4tyh-",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                            color = Color(0xFF141414),
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF172033),
                        ) {
                            Text(
                                text = "Copy link",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF141414),
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                    ),
                    color = Color(0xFF141414),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                    color = Color(0xFF5D5C5D),
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF141414),
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
fun ChatHero() {
    val dmSans = dmSansFontFamily()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .border(2.dp, EnergyPalette.Amber, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "⚡",
                color = EnergyPalette.Amber,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Ask EnergyIQ anything about\nyour power system",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.2).sp,
            ),
            color = Color(0xFF121212),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "EnergyIQ analyzes your inverter and energy data to explain battery drain, generator usage, savings, and solar performance in simple language.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 12.sp,
            ),
            color = Color(0xFF2A2F3C),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.82f),
        )
    }
}

@Composable
fun SuggestedQuestionSection(
    onSuggestionSelected: (String) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    var isExpanded by remember { mutableStateOf(true) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 180f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "suggestedQuestionsArrowRotation",
    )
    val suggestions = listOf(
        "Why did my battery drain fast last night?" to
            "Analyze overnight usage and identify what consumed the most power.",
        "Is my inverter overloaded?" to
            "Identify dangerous load spikes and system overload periods.",
        "Are my solar panels underperforming?" to
            "Detect weather impact, shading issues, or reduced solar output.",
    )

    Column {
        Row(
            modifier = Modifier.clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Suggested Questions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                ),
                color = Color(0xFF2A2F3C),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier
                    .size(18.dp)
                    .rotate(arrowRotation),
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
            ) + fadeIn(
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
            ) + fadeOut(animationSpec = tween(durationMillis = 260)),
        ) {
            Column {
                Spacer(modifier = Modifier.height(14.dp))
                suggestions.forEachIndexed { index, (title, description) ->
                    SuggestedQuestionCard(
                        title = title,
                        description = description,
                        onClick = { onSuggestionSelected(title) },
                    )
                    if (index != suggestions.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestedQuestionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    val dmSans = dmSansFontFamily()

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 114.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                ),
                color = Color(0xFF121212),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                ),
                color = Color(0xFF5D5C5D),
            )
        }
    }
}

@Composable
fun ChatComposer(
    value: String,
    attachments: List<ChatAttachment>,
    attachmentController: ChatAttachmentController,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onRemoveAttachment: (ChatAttachment) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    var isAttachmentMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp, vertical = 8.dp),
        ) {
            if (attachments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    attachments.forEach { attachment ->
                        AttachmentChip(
                            attachment = attachment,
                            onRemove = { onRemoveAttachment(attachment) },
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .padding(start = 24.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add attachment",
                            tint = Color(0xFF141414),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { isAttachmentMenuExpanded = true },
                        )
                        DropdownMenu(
                            expanded = isAttachmentMenuExpanded,
                            onDismissRequest = { isAttachmentMenuExpanded = false },
                            modifier = Modifier
                                .width(151.dp)
                                .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            containerColor = Color.White,
                            shadowElevation = 18.dp,
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Add Photo or files",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = dmSans,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            lineHeight = 14.sp,
                                        ),
                                        color = Color(0xFF141414),
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.AttachFile,
                                        contentDescription = null,
                                        tint = Color(0xFF222222),
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                                onClick = {
                                    isAttachmentMenuExpanded = false
                                    attachmentController.pickPhotoOrFiles()
                                },
                            )
                            HorizontalDivider(
                                color = Color(0xFFE5E7EB),
                                thickness = 1.dp,
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Take a screenshot",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = dmSans,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            lineHeight = 14.sp,
                                        ),
                                        color = Color(0xFF141414),
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.PhotoCamera,
                                        contentDescription = null,
                                        tint = Color(0xFF222222),
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                                onClick = {
                                    isAttachmentMenuExpanded = false
                                    attachmentController.takeScreenshot()
                                },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF141414),
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                        ),
                        decorationBox = { innerTextField ->
                            if (value.isBlank()) {
                                Text(
                                    text = "Ask anything about\nyour energy system",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                    ),
                                )
                            }
                            innerTextField()
                        },
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Icon(
                        imageVector = Icons.Outlined.MicNone,
                        contentDescription = "Voice input",
                        tint = Color(0xFF141414),
                        modifier = Modifier.size(30.dp),
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF121212))
                            .padding(12.dp)
                            .clickable(onClick = onSend),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .offset(y = (-1).dp)
                                .rotate(-45f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationThread(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        messages.forEach { message ->
            ChatMessageBubble(message = message)
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
) {
    val dmSans = dmSansFontFamily()
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        if (message.isUser) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 4.dp,
                    ),
                    color = Color(0xFF3A3A3A),
                    modifier = Modifier.width(220.dp),
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = dmSans,
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                            ),
                            color = Color(0xFFA3A3A3),
                            modifier = Modifier.align(Alignment.End),
                        )
                    }
                }
                SpeakerBadge(label = "AA")
            }
        } else {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SpeakerBadge(label = "AI")
                when (message.kind) {
                    ChatMessageKind.ALERT_SUMMARY -> AlertSummaryCard(message = message)
                    ChatMessageKind.FOLLOW_UP -> FollowUpCard(message = message)
                    ChatMessageKind.PLAIN -> StandardBotCard(message = message)
                }
            }
        }
    }
}

@Composable
fun StandardBotCard(message: ChatMessage) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF7F7F8),
        modifier = Modifier.width(252.dp),
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSansFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
            color = Color(0xFF121212),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
fun AlertSummaryCard(message: ChatMessage) {
    val dmSans = dmSansFontFamily()
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
        modifier = Modifier.width(252.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "I’ve pulled the alert details:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = Color(0xFF121212),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "⚠  Critical - Battery at 3%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                        ),
                        color = Color(0xFFDC2626),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                        ),
                        color = Color(0xFF2A2F3C),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                ),
                color = Color(0xFF9CA3AF),
            )
        }
    }
}

@Composable
fun FollowUpCard(message: ChatMessage) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
        modifier = Modifier.width(252.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = Color(0xFF2A2F3C),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSansFontFamily(),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                ),
                color = Color(0xFF9CA3AF),
            )
        }
    }
}

@Composable
fun SpeakerBadge(label: String) {
    Surface(
        modifier = Modifier.size(34.dp),
        shape = CircleShape,
        color = Color(0xFFF3F4F6),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                color = Color(0xFF4B5563),
            )
        }
    }
}

@Composable
fun AttachmentChip(
    attachment: ChatAttachment,
    onRemove: () -> Unit,
) {
    val icon = when (attachment.type) {
        ChatAttachmentType.SCREENSHOT -> Icons.Outlined.PhotoCamera
        else -> Icons.Outlined.AttachFile
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF7F7F8),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF5D5C5D),
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = attachment.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSansFontFamily(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                ),
                color = Color(0xFF2A2F3C),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(120.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remove attachment",
                tint = Color(0xFF5D5C5D),
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onRemove),
            )
        }
    }
}

@Composable
fun ChatListTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "≡",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4B5563),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = Color(0xFF4B5563),
                modifier = Modifier.size(18.dp),
            )
            Surface(
                modifier = Modifier.size(26.dp),
                shape = CircleShape,
                color = Color(0xFFF6D6A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AJ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1F2937),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = if (selected) Color(0xFFFFF6E5) else Color(0xFFFDFDFD),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 7.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = Color(0xFF5D5C5D),
            )
        }
    }
}

@Composable
fun SmallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
    ) {
        Box(
            modifier = Modifier.padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
fun SmallActionButton(
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
    ) {
        Box(
            modifier = Modifier.padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
fun ConversationSection(
    title: String,
    items: List<ChatConversationSummary>,
    onOpenConversation: (String) -> Unit,
    activeMenuConversationId: String?,
    onMenuConversationChange: (String?) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8E8)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                ),
                color = Color(0xFF999999),
            )
            Spacer(modifier = Modifier.height(14.dp))
            items.forEachIndexed { index, item ->
                ConversationCard(
                    conversation = item,
                    onClick = { onOpenConversation(item.id) },
                    isMenuExpanded = activeMenuConversationId == item.id,
                    onMenuExpandedChange = { expanded ->
                        onMenuConversationChange(if (expanded) item.id else null)
                    },
                )
                if (index != items.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ConversationCard(
    conversation: ChatConversationSummary,
    onClick: () -> Unit,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: (Boolean) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = Color(0xFFEDEDED),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when (conversation.icon) {
                        ChatConversationIcon.DANGER -> DangerVectorIcon(
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                            tint = Color(0xFF141414),
                        )
                        ChatConversationIcon.BATTERY_CHARGING -> BatteryChargingIcon(
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.2).sp,
                    ),
                    color = Color(0xFF171717),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                    color = Color(0xFF666666),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = conversation.tagColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = conversation.tag,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                        ),
                        color = conversation.tagColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = conversation.timestamp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        ),
                        color = Color(0xFF999999),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                            tint = Color(0xFF8A9099),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onMenuExpandedChange(true) },
                        )
                        ConversationCardMenu(
                            expanded = isMenuExpanded,
                            onDismiss = { onMenuExpandedChange(false) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationCardMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val items = listOf(
        "Share" to Icons.Outlined.Share,
        "Rename" to Icons.Outlined.Edit,
        "Pin Chat" to Icons.Outlined.PushPin,
        "Archive" to Icons.Outlined.Inventory2,
        "Delete" to Icons.Outlined.DeleteOutline,
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(151.dp)
            .background(Color.White)
            .clickable(enabled = false) {}
            .padding(0.dp),
        shape = RoundedCornerShape(8.dp),
        containerColor = Color.White,
        shadowElevation = 20.dp,
    ) {
        items.forEachIndexed { index, (label, icon) ->
            val isDelete = label == "Delete"
            DropdownMenuItem(
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                        ),
                        color = if (isDelete) Color(0xFFDC2626) else Color(0xFF141414),
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDelete) Color(0xFFDC2626) else Color(0xFF222222),
                        modifier = Modifier.size(20.dp),
                    )
                },
                onClick = onDismiss,
            )
            if (index != items.lastIndex) {
                HorizontalDivider(
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp,
                )
            }
        }
    }
}
