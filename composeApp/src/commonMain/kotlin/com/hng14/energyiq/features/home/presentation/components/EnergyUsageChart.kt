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
import com.hng14.energyiq.features.home.data.remote.dto.InverterHistoryItem
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun EnergyUsageChart(
    history: List<InverterHistoryItem>
) {
    if (history.isEmpty()) return

    val generated = history.map { it.solarKwh.toFloat() }
    val used = history.map { it.avgLoadKw.toFloat() }
    val labels = history.map { item ->
        runCatching {
            val dateTime = Instant.parse(item.date).toLocalDateTime(TimeZone.currentSystemDefault())
            val day = dateTime.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            day
        }.getOrDefault("...")
    }

    val maxGen = generated.maxOrNull() ?: 0f
    val maxUsed = used.maxOrNull() ?: 0f
    val maxValue = (maxGen.coerceAtLeast(maxUsed) * 1.2f).coerceAtLeast(10f)

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
            val step = maxValue / 5
            (5 downTo 0).forEach { i ->
                Text(
                    text = (step * i).toInt().toString(),
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

                (0..5).forEach { i ->
                    val y = height - ((i * (maxValue / 5)) / maxValue * height)
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

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

                // Highlight last point
                val lastIndex = generated.size - 1
                if (lastIndex >= 0) {
                    val highlight = point(lastIndex, generated[lastIndex])
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
                }
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
