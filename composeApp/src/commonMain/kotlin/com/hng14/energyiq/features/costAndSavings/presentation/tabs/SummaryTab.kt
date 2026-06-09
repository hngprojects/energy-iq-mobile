package com.hng14.energyiq.features.costAndSavings.presentation.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.InsightOutlinedCard
import com.hng14.energyiq.features.costAndSavings.presentation.CostAndSavingsUiState
import com.hng14.energyiq.features.costAndSavings.presentation.components.SavingsInsightBanner
import com.hng14.energyiq.features.costAndSavings.presentation.components.SummaryStatCard
import com.hng14.energyiq.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SummaryTab(
    uiState: CostAndSavingsUiState,
    onCheckCalculator: () -> Unit,
    dmSans: FontFamily
) {
    Column {
        // Summary Stats Stack
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryStatCard(
                title = stringResource(Res.string.cas_total_saved),
                value = uiState.totalSaved,
                trend = uiState.totalSavedTrend,
                icon = { Icon(Icons.Default.LocalGasStation, null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp)) },
                iconContainerColor = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            )
            SummaryStatCard(
                title = stringResource(Res.string.cas_energy_consumed),
                value = uiState.energyConsumed,
                trend = uiState.energyTrend,
                icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(24.dp)) },
                iconContainerColor = Color(0xFFEDE9FE),
                modifier = Modifier.fillMaxWidth()
            )
            SummaryStatCard(
                title = stringResource(Res.string.cas_generation_today),
                value = uiState.generationToday,
                trend = uiState.generationRemaining,
                icon = { Icon(Icons.Default.FlashOn, null, tint = Color(0xFFDB2777), modifier = Modifier.size(24.dp)) },
                iconContainerColor = Color(0xFFFCE7F3),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Check Calculator Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onCheckCalculator() },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF111827),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.cas_check_calculator),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                        ),
                        color = Color.White,
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SavingsInsightBanner(text = "Your savings are calculated based on your energy usage and fuel price")
    }
}
