package com.hng14.energyiq.features.costAndSavings.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.ui.InsightOutlinedCard

@Composable
fun CumulativeMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    iconContainerColor: Color,
    percentageChange: String? = null,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
) {
    InsightOutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shapeSize = 16.dp,
        paddingValues = PaddingValues(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconContainerColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                if (percentageChange != null) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = percentageChange,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF111827)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            )
        }
    }
}

@Composable
fun TrendMetricCard(
    title: String,
    value: String,
    subValue: String,
    trendLabel: String,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
) {
    InsightOutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shapeSize = 16.dp,
        paddingValues = PaddingValues(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )
                )
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF111827)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                )
            }
            
            Text(
                text = trendLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    color = Color(0xFF6B7280)
                )
            )
        }
    }
}
