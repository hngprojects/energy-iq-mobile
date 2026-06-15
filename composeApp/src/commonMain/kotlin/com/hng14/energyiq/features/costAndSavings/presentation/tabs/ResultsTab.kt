package com.hng14.energyiq.features.costAndSavings.presentation.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun ResultsTab(
    uiState: CostAndSavingsUiState,
    dmSans: FontFamily,
    onViewCumulativeTracker: () -> Unit
) {
    val savingsTitle = when (uiState.selectedTimeframe.lowercase()) {
        "weekly" -> "Weekly Savings"
        "monthly" -> "Monthly Savings"
        else -> "Daily Savings"
    }
    val savingsValue = when (uiState.selectedTimeframe.lowercase()) {
        "weekly" -> uiState.resultsWeeklySavings
        "monthly" -> uiState.resultsMonthlySavings
        else -> uiState.resultsDailySavings
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Savings Cards
        ResultSavingCard(
            title = savingsTitle,
            value = savingsValue,
            subtitle = "Sum for this period",
            dmSans = dmSans,
        )

        ResultSavingCard(
            title = "Generator Cost Avoided",
            value = uiState.resultsGeneratorCostAvoided,
            subtitle = "Based on solar offset",
            dmSans = dmSans,
        )

        ResultSavingCard(
            title = "CO2 avoided",
            value = uiState.resultsCo2Avoided,
            subtitle = "Avoided for this period",
            dmSans = dmSans,
        )

        // Period cost breakdown
        PeriodCostBreakdownTable(
            activeHours = uiState.resultsTotalActiveHours,
            equivalentPower = uiState.resultsEquivalentPower,
            dmSans = dmSans,
        )

        // Bottom Actions
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onViewCumulativeTracker,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111827),
                    contentColor = Color.White,
                ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View cumulative saving tracker",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = dmSans,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ResultSavingCard(
    title: String,
    value: String,
    subtitle: String,
    dmSans: FontFamily,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
        color = Color.White,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = dmSans,
                    color = Color(0xFF4B5563),
                    fontSize = 16.sp,
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                ),
                color = Color(0xFFF59E0B),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                ),
            )
        }
    }
}

@Composable
fun PeriodCostBreakdownTable(
    activeHours: String,
    equivalentPower: String,
    dmSans: FontFamily,
) {
    InsightOutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        paddingValues = PaddingValues(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Period cost breakdown",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                ),
                color = Color(0xFF111827),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Total Active hours",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            color = Color(0xFF111827),
                        )
                    )
                }
                Text(
                    text = activeHours,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        color = Color(0xFF6B7280),
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Equivalent power generated within active hours",
                        modifier = Modifier.width(200.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = dmSans,
                            color = Color(0xFF111827),
                        )
                    )
                }
                Text(
                    text = equivalentPower,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = dmSans,
                        color = Color(0xFF6B7280),
                    )
                )
            }
        }
    }
}
