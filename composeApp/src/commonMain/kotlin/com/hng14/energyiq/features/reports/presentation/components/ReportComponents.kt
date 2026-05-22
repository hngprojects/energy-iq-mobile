package com.hng14.energyiq.features.reports.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PieChartOutline
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.BatteryChargingIcon
import com.hng14.energyiq.core.ui.DangerVectorIcon
import com.hng14.energyiq.core.ui.InsightButtonVariant
import com.hng14.energyiq.core.ui.InsightOutlinedCard
import com.hng14.energyiq.core.ui.InsightSmallButton
import com.hng14.energyiq.core.ui.InsightStatCard
import com.hng14.energyiq.core.ui.InsightStatusChip
import com.hng14.energyiq.core.ui.SunIcon
import com.hng14.energyiq.features.reports.domain.model.ReportIcon
import com.hng14.energyiq.features.reports.domain.model.ReportItem
import com.hng14.energyiq.features.reports.domain.model.ReportStat
import com.hng14.energyiq.features.reports.domain.model.ReportStatus

@Composable
fun ReportStatCard(
    stat: ReportStat,
    modifier: Modifier = Modifier,
    valueFontSize :TextUnit = 18.sp
) {
    val dmSans = dmSansFontFamily()
    val titleFontSize = if (stat.showReportSentIcon) 12.sp else 14.sp
    val titleLineHeight = if (stat.showReportSentIcon) 18.sp else 21.sp
    InsightStatCard(
        title = stat.title,
        value = stat.value,
        subtitle = stat.subtitle,
        dotColor = stat.dotColor,
        modifier = modifier,
        fontFamily = dmSans,
        percentChange = stat.percentageChange,

        hideDot = stat.showSunIcon ||
            stat.showReportSentIcon ||
            stat.showBatteryChargingIcon ||
            stat.showDangerVectorIcon,
        titleLeading = when {
            stat.showSunIcon -> {
                { SunIcon(modifier = Modifier.size(18.dp)) }
            }

            stat.showReportSentIcon -> {
                {
                    Icon(
                    imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            stat.showBatteryChargingIcon -> {
                {
                    BatteryChargingIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF17CC4E),
                        contentDescription = null,
                    )
                }
            }

            stat.showDangerVectorIcon -> {
                {
                    DangerVectorIcon(
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFF5A623),
                        contentDescription = null,
                    )
                }
            }

            else -> null
        },
        titleFontSize = titleFontSize,
        titleLineHeight = titleLineHeight,
        valueFontSize = valueFontSize,
    )
}

@Composable
fun ReportToolbar(
    modifier: Modifier = Modifier,
    onGenerateReport: () -> Unit,
) {
    val dmSans = dmSansFontFamily()
    var selectedType by remember { mutableStateOf("Report Type") }
    var selectedSchedule by remember { mutableStateOf("Schedule") }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        ReportDropdownChip(
            label = selectedType,
            fontFamily = dmSans,
            onClick = { selectedType = if (selectedType == "Report Type") "Weekly" else "Report Type" },
            modifier = Modifier.width(160.dp),
            height = 40.dp,
            shapeSize = 12.dp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End,
        ) {
            InsightSmallButton(
                label = selectedSchedule,
                onClick = { selectedSchedule = if (selectedSchedule == "Schedule") "Auto" else "Schedule" },
                modifier = Modifier.width(190.dp),
                variant = InsightButtonVariant.Secondary,
                fontFamily = dmSans,
            )

            Surface(
                modifier = Modifier
                    .height(44.dp)
                    .width(190.dp)
                    .clickable(onClick = onGenerateReport),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF111827),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Generate Report",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        ),
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportDropdownChip(
    label: String,
    fontFamily: FontFamily,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    shapeSize: Dp = 14.dp,
    centerLabel: Boolean = false,
) {
    Surface(
        modifier = modifier
            .height(height)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(shapeSize),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                ),
                color = Color(0xFF5D5C5D),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = if (centerLabel) TextAlign.Center else TextAlign.Start,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun ReportCard(
    item: ReportItem,
    onView: (String) -> Unit,
    onDownload: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dmSans = dmSansFontFamily()
    val ready = item.status == ReportStatus.READY
    InsightOutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shapeSize = 12.dp,
    ) {
        Column {
            when (item.status) {
                ReportStatus.READY -> InsightStatusChip(
                    label = "Ready",
                    background = Color(0xFFECFDF3),
                    foreground = Color(0xFF16A34A),
                    dot = Color(0xFF22C55E),
                    fontFamily = dmSans,
                )
                ReportStatus.PROCESSING -> InsightStatusChip(
                    label = "Processing",
                    background = Color(0xFFFFF7E6),
                    foreground = Color(0xFFD97706),
                    dot = Color(0xFFF59E0B),
                    fontFamily = dmSans,
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReportCardGlyph(icon = item.icon)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        ),
                        color = Color(0xFF171717),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.dateRange,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                        ),
                        color = Color(0xFF525252),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.height(27.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ReportCardActionButton(
                    label = "View",
                    enabled = ready,
                    onClick = { onView(item.id) },
                    modifier = Modifier.weight(1f),
                    background = Color(0xFF111827),
                    foreground = Color.White,
                    fontFamily = dmSans,
                )
                ReportCardActionButton(
                    label = "Download",
                    enabled = ready,
                    onClick = { onDownload(item.id) },
                    modifier = Modifier.weight(1f),
                    background = Color(0xFFE5E7EB),
                    foreground = Color(0xFF111827),
                    fontFamily = dmSans,
                )
            }
        }
    }
}

@Composable
private fun ReportCardActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    background: Color,
    foreground: Color,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    val bg = if (enabled) background else Color(0xFFF3F4F6)
    val fg = if (enabled) foreground else Color(0xFF9CA3AF)
    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = bg,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
                color = fg,
            )
        }
    }
}

@Composable
private fun ReportCardGlyph(
    icon: ReportIcon,
) {
    val tint = Color(0xFF111827)
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = Color(0xFFEDEDED),
    ) {
        Box(contentAlignment = Alignment.Center) {
            val imageVector = when (icon) {
                ReportIcon.SOLAR -> Icons.Outlined.WbSunny
                ReportIcon.ENERGY -> Icons.Outlined.ElectricBolt
                ReportIcon.SUMMARY -> Icons.Outlined.Description
                ReportIcon.BREAKDOWN -> Icons.Outlined.PieChartOutline
            }
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
