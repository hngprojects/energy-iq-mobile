package com.hng14.energyiq.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.Res
import com.hng14.energyiq.core.theme.EnergyPalette
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.ExpandVectorIcon
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    showSunIcon: Boolean = false,
    showRunningLowIcon: Boolean = false,
    wrapSubtitleInContainer: Boolean = false,
    badgeText: String? = null,
    badgeColor: Color = Color.Transparent,
    badgeContentColor: Color = Color.Unspecified,
) {
    val kilowatts = stringResource(Res.string.dashboard_kilowatts)
    val accessibleValue = value.replace("kW", kilowatts)
    val accessibleBadge = badgeText ?: "Unknown"
    val contentDesc = stringResource(
        Res.string.dashboard_metric_description,
        title,
        accessibleValue,
        subtitle,
        accessibleBadge,
    )

    DashboardCard(
        horizontalPadding = 24.dp,
        verticalPadding = 24.dp,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = contentDesc
        }
    ) {
        LabelText(
            text = title,
            showSunIcon = showSunIcon,
            showRunningLowIcon = showRunningLowIcon,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
            ),
            color = Color(0xFF111827)
        )
        if (wrapSubtitleInContainer) {
            Spacer(modifier = Modifier.height(24.dp))
            SubtitlePill(text = subtitle)
        } else {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
            )
        }
        if (badgeText != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Badge(
                text = badgeText,
                containerColor = badgeColor,
                contentColor = badgeContentColor,
            )
        }
    }
}

@Composable
internal fun BatteryCard(
    soc: Double?,
    subtitle: String = "Usage remaining",
) {
    val socSafe = soc ?: 0.0
    val socClamped = socSafe.coerceIn(0.0, 100.0)
    val socRaw = socClamped.toString()
    val socPretty = if (socRaw.contains('.')) socRaw.trimEnd('0').trimEnd('.') else socRaw
    val socText = "$socPretty%"

    val percentStr = stringResource(Res.string.dashboard_percent)
    val accessibleSoc = "$socPretty $percentStr"
    val contentDesc = stringResource(Res.string.dashboard_battery_description, accessibleSoc, subtitle)

    DashboardCard(
        horizontalPadding = 24.dp,
        verticalPadding = 24.dp,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = contentDesc
        }
    ) {
        LabelText(
            text = "Battery",
            showBatteryChargingIcon = true,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = socText,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
            ),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProgressBar(
            progress = (socClamped / 100).toFloat(),
            color = if (socClamped > 20) Color(0xFF0E9F6E) else EnergyPalette.Danger
        )
        Spacer(modifier = Modifier.height(30.dp))
        Badge(
            text = subtitle,
            containerColor = if (socClamped > 20) Color(0xFFDDF7E6) else Color(0xFFFDEAEA),
            contentColor = if (socClamped > 20) EnergyPalette.BatteryGreen else EnergyPalette.Danger,
        )
    }
}

@Composable
internal fun PowerUsageCard() {
    val dmSans = dmSansFontFamily()
    val items = listOf(
        PowerUsageItem("Cold room", "44%", PowerUsageIcon.Ac),
        PowerUsageItem("Back room AC", "29%", PowerUsageIcon.Ac),
        PowerUsageItem("Lighting", "15%", PowerUsageIcon.Bulb),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
    ) {
        Column {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Where your power's going",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            letterSpacing = TextUnit.Unspecified,
                        ),
                        color = Color(0xFF1A1A1A),
                    )
                    LivePill()
                }

                Spacer(modifier = Modifier.height(18.dp))
                items.forEachIndexed { index, item ->
                    PowerUsageListRow(item = item)
                    if (index != items.lastIndex) {
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF1F1F1)),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ExpandVectorIcon(
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "View breakdown",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            letterSpacing = TextUnit.Unspecified,
                        ),
                        color = Color(0xFF6B7280),
                    )
                }
                Text(
                    text = "All 5 zones",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        letterSpacing = TextUnit.Unspecified,
                    ),
                    color = Color(0xFFB6B8BD),
                )
            }
        }
    }
}

@Composable
internal fun SavingsOverviewCard(
    savedToday: Double?,
    savedMonth: Double?,
) {
    val savedTodaySafe = savedToday ?: 0.0
    val savedMonthSafe = savedMonth ?: 0.0

    val todayDesc = stringResource(Res.string.dashboard_savings_today_description, savedTodaySafe.toInt().toString())
    val monthDesc = stringResource(Res.string.dashboard_savings_month_description, savedMonthSafe.toInt().toString())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE7E5E4)),
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = todayDesc
                    },
            ) {
                Text(
                    text = "You saved today",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NGN ${savedTodaySafe.toInt()}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                    ),
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Savings generated from solar\nand battery usage today",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 28.sp,
                    ),
                    color = Color(0xFF6B7280),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF1F1F1)),
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = monthDesc
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Saved this month",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                            ),
                            color = Color(0xFF6B7280),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "NGN ${savedMonthSafe.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                            ),
                            color = Color(0xFF111827),
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.ArrowOutward,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun EnergyUsageCard(
    history: List<com.hng14.energyiq.features.home.data.remote.dto.InverterHistoryItem>
) {
    DashboardCard {
        Text(
            text = "Energy usage",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "How much your panels generated\nvs how much power you used.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9CA3AF),
        )
        Spacer(modifier = Modifier.height(14.dp))
        EnergyUsageChart(history = history)
    }
}
