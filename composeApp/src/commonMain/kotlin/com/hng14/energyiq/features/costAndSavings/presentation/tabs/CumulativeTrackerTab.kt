package com.hng14.energyiq.features.costAndSavings.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.*
import com.hng14.energyiq.features.costAndSavings.presentation.CostAndSavingsUiState
import com.hng14.energyiq.features.costAndSavings.presentation.components.CumulativeMetricCard
import com.hng14.energyiq.features.costAndSavings.presentation.components.TrendMetricCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun CumulativeTrackerTab(
    uiState: CostAndSavingsUiState,
    dmSans: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Header
        Text(
            text = stringResource(Res.string.cas_cumulative_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF111827)
        )
        Text(
            text = stringResource(Res.string.cas_cumulative_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                color = Color(0xFF6B7280)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Metric Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CumulativeMetricCard(
                title = stringResource(Res.string.cas_lifetime_savings),
                value = uiState.lifetimeSavings,
                icon = Icons.Outlined.Savings,
                iconColor = Color(0xFFF59E0B),
                iconContainerColor = Color(0xFFFEF3C7),
                percentageChange = uiState.lifetimeSavingsTrend,
                fontFamily = dmSans
            )

            CumulativeMetricCard(
                title = stringResource(Res.string.cas_co2_avoided),
                value = uiState.co2Avoided,
                icon = Icons.Outlined.Eco,
                iconColor = Color(0xFF10B981),
                iconContainerColor = Color(0xFFD1FAE5),
                fontFamily = dmSans
            )

            CumulativeMetricCard(
                title = stringResource(Res.string.cas_gen_hours_avoided),
                value = uiState.genHoursAvoided,
                icon = Icons.Outlined.Timer,
                iconColor = Color(0xFFF59E0B),
                iconContainerColor = Color(0xFFFFF7ED),
                fontFamily = dmSans
            )

            CumulativeMetricCard(
                title = stringResource(Res.string.cas_fuel_saved),
                value = uiState.fuelSaved,
                icon = Icons.Outlined.EvStation,
                iconColor = Color(0xFFEF4444),
                iconContainerColor = Color(0xFFFEE2E2),
                fontFamily = dmSans
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Savings Trends & Analytics Section
        Text(
            text = stringResource(Res.string.cas_trends_analytics_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = Color(0xFF111827)
        )
        Text(
            text = stringResource(Res.string.cas_trends_analytics_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = dmSans,
                color = Color(0xFF6B7280)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TrendMetricCard(
                title = stringResource(Res.string.cas_total_savings_to_date),
                value = uiState.totalSavingsToDate,
                subValue = uiState.totalSavingsTrend,
                trendLabel = stringResource(Res.string.cas_vs_previous_period),
                fontFamily = dmSans
            )

            TrendMetricCard(
                title = stringResource(Res.string.cas_avg_monthly_savings),
                value = uiState.avgMonthlySavings,
                subValue = uiState.avgMonthlySavingsTrend,
                trendLabel = uiState.efficiencyTrendLabel,
                fontFamily = dmSans
            )
        }
    }
}
