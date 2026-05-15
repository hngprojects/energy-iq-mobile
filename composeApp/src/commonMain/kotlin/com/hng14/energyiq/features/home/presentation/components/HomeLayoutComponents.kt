package com.hng14.energyiq.features.home.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    FloatingActionButton(
        onClick = {},
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
        containerColor = Color(0xFF111827),
        contentColor = Color.White,
        shape = CircleShape,
    ) {
        ChatBotVectorIcon(
            modifier = Modifier.size(24.dp),
            contentDescription = "Chatbot",
        )
    }
}

@Composable
internal fun HomeTopBar(name: String?) {
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
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF2A2F3C),
            modifier = Modifier.size(18.dp),
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            BellIcon(
                contentDescription = "Notifications",
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = Color(0xFFFFD3A5),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2A2F3C),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun WarningBanner() {
    val dmSans = dmSansFontFamily()

    Surface(
        color = Color(0xFFFCEAEA),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC61C15)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            DangerVectorIcon(
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
            Text(
                text = "Your battery will run flat by 10am. Switch off the AC in the back room now.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    letterSpacing = TextUnit.Unspecified,
                ),
                color = Color(0xFFC81E1E),
                modifier = Modifier.weight(1f, fill = true),
            )
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Dismiss warning",
                tint = Color(0xFFC61C15),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}
