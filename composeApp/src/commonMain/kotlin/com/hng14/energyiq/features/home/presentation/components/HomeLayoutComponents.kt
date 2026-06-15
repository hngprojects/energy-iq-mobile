package com.hng14.energyiq.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import com.hng14.energyiq.core.util.toNonScaledSp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.BellIcon
import com.hng14.energyiq.core.ui.ChatBotVectorIcon
import com.hng14.energyiq.core.ui.DangerVectorIcon
import kotlin.math.roundToInt

@Composable
internal fun DraggableFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dmSans = dmSansFontFamily()

    val fontScale = LocalDensity.current.fontScale.coerceAtMost(1.8f)
    val fabSize = (72 * fontScale).dp


    Surface(
        onClick = onClick,
        modifier = modifier
            .size(fabSize)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .semantics {
                role = Role.Button
                contentDescription = "Open AI Chat"
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
        shape = CircleShape,
        color = Color(0xFF916231),
        border = BorderStroke(4.dp, Color(0xFFE9E5E2))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\u26A1",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp
                ),
                color = Color.White
            )
            Text(
                text = "AI Chat",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun HomeTopBar(
    name: String?,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val initials = name
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.ifBlank { "U" }
        ?: "U"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        com.hng14.energyiq.core.ui.EnergyIqBrandMark(
            horizontalArrangement = Arrangement.Start
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(32.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Profile settings"
                    }
                    .clickable { onProfileClick() },
                shape = CircleShape,
                color = Color(0xFFFFD3A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.dp.toNonScaledSp(),
                        color = Color(0xFF2A2F3C),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun BackHomeTopBar(
    title: String,
    name: String?,
    onBack: (() -> Unit)? = null,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val dmSans = dmSansFontFamily()
    val initials = name
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.ifBlank { "U" }
        ?: "U"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111827),
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Color(0xFF111827),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics{
                        role = Role.Button
                        contentDescription = "Notifications"
                    }
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(24.dp)

                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(32.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Profile settings"
                    }
                    .clickable { onProfileClick() },
                shape = CircleShape,
                color = Color(0xFFFFD3A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.dp.toNonScaledSp(),
                        color = Color(0xFF2A2F3C),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun WarningBanner(
    reason: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dmSans = dmSansFontFamily()

    Surface(
        color = Color(0xFFFCEAEA),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, Color(0xFFC61C15)),
        modifier = modifier.semantics { 
            liveRegion = LiveRegionMode.Assertive 
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DangerVectorIcon(
                contentDescription = "System alert",
                modifier = Modifier.size(30.dp),
            )
            Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    letterSpacing = TextUnit.Unspecified,
                ),
                color = Color(0xFFC81E1E),
                modifier = Modifier.weight(1f, fill = true),
            )
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Dismiss warning",
                tint = Color(0xFFC61C15),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .semantics { role = Role.Button }
                    .clickable { onDismiss() },
            )
        }
    }
}
