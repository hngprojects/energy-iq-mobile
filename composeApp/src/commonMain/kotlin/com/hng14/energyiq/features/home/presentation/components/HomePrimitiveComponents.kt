package com.hng14.energyiq.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.BatteryChargingIcon
import com.hng14.energyiq.core.ui.BulbVectorIcon
import com.hng14.energyiq.core.ui.RunningLowIcon
import com.hng14.energyiq.core.ui.SunIcon

@Composable
internal fun DashboardCard(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            content = content,
        )
    }
}

@Composable
internal fun LabelText(
    text: String,
    showSunIcon: Boolean = false,
    showBatteryChargingIcon: Boolean = false,
    showRunningLowIcon: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (showSunIcon) {
            SunIcon(contentDescription = null, modifier = Modifier.size(14.dp))
        }
        if (showBatteryChargingIcon) {
            BatteryChargingIcon(contentDescription = null, modifier = Modifier.size(14.dp))
        }
        if (showRunningLowIcon) {
            RunningLowIcon(contentDescription = null, modifier = Modifier.size(14.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280),
        )
    }
}

@Composable
internal fun Badge(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
internal fun LivePill() {
    Row(
        modifier = Modifier
            .background(Color(0xFFE8F8EC), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFF22C55E), CircleShape),
        )
        Text(
            text = "Live",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
            color = Color(0xFF22C55E),
        )
    }
}

@Composable
internal fun SubtitlePill(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF55F5F5), RoundedCornerShape(16.dp))
            .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280),
        )
    }
}

@Composable
internal fun ProgressBar(
    progress: Float,
    color: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(4.dp)
                .background(color, RoundedCornerShape(999.dp)),
        )
    }
}

internal data class PowerUsageItem(
    val label: String,
    val value: String,
    val icon: PowerUsageIcon,
)

internal enum class PowerUsageIcon {
    Ac,
    Bulb,
}

@Composable
internal fun PowerUsageListRow(item: PowerUsageItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFFEFD),
            border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                when (item.icon) {
                    PowerUsageIcon.Ac -> Icon(
                        imageVector = Icons.Outlined.AcUnit,
                        contentDescription = null,
                        tint = Color(0xFF1F2937),
                        modifier = Modifier.size(24.dp),
                    )
                    PowerUsageIcon.Bulb -> BulbVectorIcon(
                        modifier = Modifier.size(24.dp),
                        contentDescription = null,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            ),
            color = Color(0xFF33353A),
            modifier = Modifier.weight(1f),
        )

        Text(
            text = item.value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            ),
            color = Color(0xFF6B7280),
        )
    }
}

@Composable
internal fun MonthAxis() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
            Text(
                text = month,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD1D5DB),
            )
        }
    }
}

@Composable
internal fun LegendDot(
    color: Color,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280),
        )
    }
}
