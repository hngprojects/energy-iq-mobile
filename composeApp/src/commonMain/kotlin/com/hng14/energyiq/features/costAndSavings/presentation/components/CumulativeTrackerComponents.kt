package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
fun CumulativeSavingsChart(
    actualSavings: List<Float>,
    gridProjection: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
) {
    val textMeasurer = rememberTextMeasurer()
    val axisColor = Color(0xFFF1F1F1)
    val textColor = Color(0xFF9CA3AF)
    val actualColor = Color(0xFFF59E0B)
    val projectionColor = Color(0xFFD1D5DB)

    val axisTextStyle = TextStyle(
        color = textColor,
        fontSize = 10.sp,
        fontFamily = fontFamily
    )

    Box(modifier = modifier.fillMaxWidth().height(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 40.dp.toPx()
            val paddingBottom = 20.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom

            val maxDataValue = (actualSavings + gridProjection).maxOrNull() ?: 1200000f
            val maxData = if (maxDataValue < 1000f) 1000f else maxDataValue * 1.2f
            
            val stepsY = 5
            val stepHeight = chartHeight / (stepsY - 1)

            // Y-axis labels and grid lines
            for (i in 0 until stepsY) {
                val y = height - paddingBottom - (i * stepHeight)
                val value = (i * (maxData / (stepsY - 1)))
                val label = if (value >= 1000000) "₦${(value / 1000000.0).let { ((it * 10).toInt() / 10.0) }}M"
                           else "₦${(value / 1000).toInt()}k"
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = axisTextStyle,
                    topLeft = Offset(0f, y - 6.dp.toPx())
                )
                
                drawLine(
                    color = axisColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            val stepWidth = chartWidth / (labels.size - 1)

            // X-axis labels
            labels.forEachIndexed { index, label ->
                val x = paddingLeft + (index * stepWidth)
                val textLayoutResult = textMeasurer.measure(label, axisTextStyle.copy(fontWeight = FontWeight.Bold, color = actualColor))
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = axisTextStyle.copy(fontWeight = FontWeight.Bold, color = actualColor),
                    topLeft = Offset(x - (textLayoutResult.size.width / 2f), height - paddingBottom + 4.dp.toPx())
                )
            }

            // Projection Line (Dashed)
            val projectionPoints = gridProjection.mapIndexed { index, value ->
                val x = paddingLeft + (index * stepWidth)
                val y = height - paddingBottom - (value / maxData * chartHeight)
                Offset(x, y)
            }

            if (projectionPoints.isNotEmpty()) {
                val path = Path()
                path.moveTo(projectionPoints[0].x, projectionPoints[0].y)
                for (i in 1 until projectionPoints.size) {
                    path.lineTo(projectionPoints[i].x, projectionPoints[i].y)
                }
                drawPath(
                    path = path,
                    color = projectionColor,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
            }

            // Actual Savings Line and Fill
            val actualPoints = actualSavings.mapIndexed { index, value ->
                val x = paddingLeft + (index * stepWidth)
                val y = height - paddingBottom - (value / maxData * chartHeight)
                Offset(x, y)
            }

            if (actualPoints.isNotEmpty()) {
                val path = Path()
                path.moveTo(actualPoints[0].x, actualPoints[0].y)
                for (i in 1 until actualPoints.size) {
                    val p0 = actualPoints[i-1]
                    val p1 = actualPoints[i]
                    val controlX = (p0.x + p1.x) / 2
                    path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }

                // Fill area
                val fillPath = Path()
                fillPath.addPath(path)
                fillPath.lineTo(actualPoints.last().x, height - paddingBottom)
                fillPath.lineTo(actualPoints.first().x, height - paddingBottom)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(actualColor.copy(alpha = 0.2f), Color.Transparent),
                        startY = actualPoints.minOf { it.y },
                        endY = height - paddingBottom
                    )
                )

                drawPath(
                    path = path,
                    color = actualColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
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
