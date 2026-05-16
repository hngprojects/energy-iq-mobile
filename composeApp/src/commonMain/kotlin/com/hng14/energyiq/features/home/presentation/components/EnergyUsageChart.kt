package com.hng14.energyiq.features.home.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hng14.energyiq.core.theme.EnergyPalette

@Composable
internal fun EnergyUsageChart() {
    val generated = listOf(18f, 22f, 16f, 21f, 30f, 29f, 26f)
    val used = listOf(24f, 20f, 23f, 18f, 19f, 17f, 16f)
    val labels = listOf("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")
    val maxValue = 35f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        LegendDot(color = EnergyPalette.Amber, label = "Generated")
        Spacer(modifier = Modifier.width(12.dp))
        LegendDot(color = Color(0xFFB6BDC9), label = "Used")
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            listOf(35, 28, 21, 14, 7, 0).forEach { value ->
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF),
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (labels.size - 1).coerceAtLeast(1)

                fun point(index: Int, value: Float): Offset {
                    val x = stepX * index
                    val y = height - ((value / maxValue) * height)
                    return Offset(x, y)
                }

                listOf(0f, 7f, 14f, 21f, 28f, 35f).forEach { grid ->
                    val y = height - ((grid / maxValue) * height)
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                val fridayX = stepX * 4
                drawLine(
                    color = Color(0xFFD1D5DB),
                    start = Offset(fridayX, 0f),
                    end = Offset(fridayX, height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
                )

                for (index in 0 until generated.lastIndex) {
                    drawLine(
                        color = EnergyPalette.Amber,
                        start = point(index, generated[index]),
                        end = point(index + 1, generated[index + 1]),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = Color(0xFFB6BDC9),
                        start = point(index, used[index]),
                        end = point(index + 1, used[index + 1]),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                    )
                }

                val highlight = point(4, generated[4])
                drawCircle(
                    color = EnergyPalette.Amber,
                    radius = 5.dp.toPx(),
                    center = highlight,
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = highlight,
                )

                val tooltipWidth = 76.dp.toPx()
                val tooltipHeight = 44.dp.toPx()
                val tooltipLeft = (highlight.x + 10.dp.toPx()).coerceAtMost(width - tooltipWidth)
                val tooltipTop = (highlight.y - tooltipHeight - 6.dp.toPx()).coerceAtLeast(4.dp.toPx())
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(tooltipLeft, tooltipTop),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(10.dp.toPx()),
                )
                drawRoundRect(
                    color = Color(0xFFE5E7EB),
                    topLeft = Offset(tooltipLeft, tooltipTop),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(10.dp.toPx()),
                    style = Stroke(1.dp.toPx()),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF),
                    )
                }
            }
        }
    }
}
