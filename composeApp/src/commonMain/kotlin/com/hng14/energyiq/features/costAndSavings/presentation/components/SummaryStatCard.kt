package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily
import com.hng14.energyiq.core.ui.InsightOutlinedCard

@Composable
fun SummaryStatCard(
    title: String,
    value: String,
    trend: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val dmSans = dmSansFontFamily()
    InsightOutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shapeSize = 12.dp,
        paddingValues = PaddingValues(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = dmSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        lineHeight = 18.sp,
                    ),
                    color = Color(0xFF666666)
                )
                icon()
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 33.6.sp,
                    letterSpacing = (-0.28).sp,
                ),
                color = Color(0xFF080C13)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = trend,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = dmSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    lineHeight = 21.sp,
                    color = if (trend.contains("+") || trend.contains("%")) Color(0xFF22C55E) else Color(0xFFDC2626)
                )
            )
        }
    }
}
