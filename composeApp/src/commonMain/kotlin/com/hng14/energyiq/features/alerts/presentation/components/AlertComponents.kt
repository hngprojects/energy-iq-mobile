package com.hng14.energyiq.features.alerts.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.BatteryChargingIcon
import com.hng14.energyiq.core.ui.DangerVectorIcon
import com.hng14.energyiq.core.ui.InsightButtonVariant
import com.hng14.energyiq.core.ui.InsightOutlinedCard
import com.hng14.energyiq.core.ui.InsightSmallButton
import com.hng14.energyiq.core.ui.InsightStatCard
import com.hng14.energyiq.core.ui.InsightStatusChip
import com.hng14.energyiq.features.alerts.domain.model.*

@Composable
fun AlertTypeTabs(
    selectedType: AlertType?,
    onTypeSelected: (AlertType?) -> Unit,
) {
    val dmSans = dmSansFontFamily()
    val options: List<Pair<String, AlertType?>> = buildList {
        add("All" to null)
        addAll(AlertType.entries.map { it.label to it })
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(items = options, key = { it.first }) { (label, type) ->
            val selected = type == selectedType
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (selected) Color(0xFF141D2F) else Color.White,
                border = BorderStroke(1.dp, if (selected) Color(0xFF141D2F) else Color(0xFFE5E7EB)),
                modifier = Modifier
                    .clickable { onTypeSelected(type) },
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    ),
                    color = if (selected) Color.White else Color(0xFF111827),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
fun SmartAlertStatCard(
    stat: AlertStat,
    modifier: Modifier = Modifier,
) {
    val dmSans = dmSansFontFamily()
    InsightStatCard(
        title = stat.title,
        value = stat.value,
        subtitle = stat.subtitle,
        dotColor = stat.dotColor,
        modifier = modifier,
        fontFamily = dmSans,
    )
}

@Composable
fun AlertToolbar(
    selectedFilter: AlertFilter,
    onFilterSelected: (AlertFilter) -> Unit,
    unresolvedCount: Int,
    isRefreshing: Boolean = false,
    onRefreshClick: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    var isFilterExpanded by remember { mutableStateOf(false) }
    var filterAnchorOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var filterAnchorHeight by remember { mutableStateOf(0) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(modifier = Modifier.wrapContentSize(align = Alignment.TopStart)) {
            Surface(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        filterAnchorOffset = coordinates.positionInWindow()
                        filterAnchorHeight = coordinates.size.height
                    }
                    .clickable { isFilterExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = selectedFilter.label,
                        modifier = Modifier.width(76.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        ),
                        color = Color(0xFF020618),
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF7C7C7C),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            if (isFilterExpanded) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(
                        x = filterAnchorOffset.x.toInt(),
                        y = (filterAnchorOffset.y + filterAnchorHeight).toInt(),
                    ),
                    onDismissRequest = { isFilterExpanded = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    Surface(
                        modifier = Modifier.width(151.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        shadowElevation = 20.dp,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    ) {
                        Column {
                            AlertFilter.entries.forEachIndexed { index, filter ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = filter.label,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = dmSans,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                lineHeight = 14.sp,
                                            ),
                                            color = Color(0xFF141414),
                                        )
                                    },
                                    onClick = {
                                        isFilterExpanded = false
                                        onFilterSelected(filter)
                                    },
                                )
                                if (index != AlertFilter.entries.lastIndex) {
                                    HorizontalDivider(
                                        color = Color(0xFFE5E7EB),
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFECEEF1)),
            modifier = Modifier.clickable(enabled = !isRefreshing) { onRefreshClick() }
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(6.dp)
                            .background(Color(0xFF111827), CircleShape),
                    )
                    Text(
                        text = "You have $unresolvedCount unresolved ${if (unresolvedCount == 1) "alert" else "alerts"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        ),
                        color = Color(0xFF111928),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = if (isRefreshing) "Refreshing..." else "Real-time updates",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                        ),
                        color = Color(0xFF666666),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF141D2F),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFF666666),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmartAlertCard(
    alert: SmartAlertItem,
    onInspect: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    InsightOutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shapeSize = 12.dp,
        paddingValues = PaddingValues(24.dp),
    ) {
        Column {
            SeverityBadge(severity = alert.severity)
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AlertCardGlyph(icon = alert.icon, severity = alert.severity)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                        ),
                        color = Color(0xFF171717),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = alert.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                        ),
                        color = Color(0xFF525252),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                    ),
                    color = Color(0xFF525252),
                )
                InsightSmallButton(
                    label = alert.actionLabel,
                    onClick = onInspect,
                    modifier = Modifier.size(width = 94.dp, height = 40.dp),
                    enabled = !alert.resolved,
                    variant = InsightButtonVariant.Primary,
                    fontFamily = dmSans,
                )
            }
        }
    }
}

@Composable
fun SmartAlertInspectDialog(
    content: SmartAlertDialogContent,
    onDismiss: () -> Unit,
    onPrimaryAction: () -> Unit,
    isResolving: Boolean = false,
) {
    val dmSans = dmSansFontFamily()
    Dialog(onDismissRequest = if (isResolving) ({}) else onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color(0xFFFDECEC),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                DangerVectorIcon(
                                    modifier = Modifier.size(15.dp),
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = content.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                ),
                                color = Color(0xFF141414),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = content.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                ),
                                color = Color(0xFF666666),
                            )
                            Text(
                                text = content.timestamp,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = dmSans,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                ),
                                color = Color(0xFF525252),
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF171717),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(enabled = !isResolving, onClick = onDismiss),
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    content.metrics.forEach { metric ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFAFAFA),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = metric.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp,
                                        lineHeight = 15.sp,
                                    ),
                                    color = Color(0xFF999999),
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Text(
                                    text = metric.value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        //lineHeight = 21.sp,
                                    ),
                                    color = Color(0xFF141414),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF7F7F7),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Why this alert?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                            color = Color(0xFF1F2937),
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = content.explanation,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = dmSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            ),
                            color = Color(0xFF3F3F46),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Surface(
                        modifier = Modifier
                            .size(width = 156.dp, height = 62.dp)
                            .clickable(enabled = !isResolving, onClick = onPrimaryAction),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isResolving) Color(0xFF9CA3AF) else Color(0xFF111827),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isResolving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = content.primaryActionLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = dmSans,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        lineHeight = 20.sp,
                                    ),
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(
    severity: AlertSeverity,
) {
    val dmSans = dmSansFontFamily()
    val palette = when (severity) {
        AlertSeverity.CRITICAL -> SeverityPalette("Critical", Color(0xFFFDECEC), Color(0xFFDC2626), Color(0xFFEF4444))
        AlertSeverity.WARNING -> SeverityPalette("Warning", Color(0xFFFFF7E6), Color(0xFFD97706), Color(0xFFF59E0B))
        AlertSeverity.SUCCESS -> SeverityPalette("Success", Color(0xFFECFDF3), Color(0xFF16A34A), Color(0xFF22C55E))
    }
    InsightStatusChip(
        label = palette.label,
        background = palette.background,
        foreground = palette.foreground,
        dot = palette.dot,
        fontFamily = dmSans,
    )
}

@Composable
private fun AlertCardGlyph(
    icon: AlertCardIcon,
    severity: AlertSeverity,
) {
    val tint = when (severity) {
        AlertSeverity.CRITICAL -> Color(0xFF9CA3AF)
        AlertSeverity.WARNING -> Color(0xFF6B7280)
        AlertSeverity.SUCCESS -> Color(0xFF9CA3AF)
    }
    Surface(
        modifier = Modifier.size(28.dp),
        shape = CircleShape,
        color = Color(0xFFF3F4F6),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (icon) {
                AlertCardIcon.BATTERY -> BatteryChargingIcon(
                    modifier = Modifier.size(14.dp),
                    contentDescription = null,
                )
                AlertCardIcon.FRIDGE -> Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp),
                )
                AlertCardIcon.HVAC -> Icon(
                    imageVector = Icons.Outlined.AcUnit,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp),
                )
                AlertCardIcon.AUTOMATION -> Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp),
                )
                AlertCardIcon.SOLAR -> DangerVectorIcon(
                    modifier = Modifier.size(14.dp),
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                )
            }
        }
    }
}
