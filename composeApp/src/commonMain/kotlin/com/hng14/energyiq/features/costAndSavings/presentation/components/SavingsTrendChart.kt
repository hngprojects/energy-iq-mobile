package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SavingsTrendChart(
    dataPoints: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val axisColor = Color(0xFFE5E7EB)
    val textColor = Color(0xFF6B7280)
    val lineColor = Color(0xFF16A34A)
    val dotColor = Color(0xFF16A34A)
    val dashColor = Color(0xFF16A34A)

    val textStyle = TextStyle(
        color = textColor,
        fontSize = 12.sp,
        fontFamily = fontFamily
    )

    Box(modifier = modifier.fillMaxWidth().height(250.dp).padding(top = 16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 45.dp.toPx()
            val paddingBottom = 30.dp.toPx()
            val paddingTop = 10.dp.toPx()
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom - paddingTop

            val maxData = 30000f // Scaling based on the ₦25k in image
            val stepsY = 6
            val stepHeight = chartHeight / (stepsY - 1)

            // Draw Y-axis labels and horizontal lines
            for (i in 0 until stepsY) {
                val y = height - paddingBottom - (i * stepHeight)
                val value = (i * 5000)
                val label = "₦${value / 1000}k"
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = textStyle,
                    topLeft = Offset(0f, y - 10.dp.toPx())
                )
                
                // Optional: light grid lines
                // drawLine(axisColor, Offset(paddingLeft, y), Offset(width, y), strokeWidth = 1.dp.toPx())
            }

            // Draw X-axis labels
            val stepWidth = chartWidth / (dataPoints.size - 1)
            dataPoints.forEachIndexed { index, _ ->
                val x = paddingLeft + (index * stepWidth)
                if (index < labels.size) {
                    val label = labels[index]
                    val textLayoutResult = textMeasurer.measure(label, textStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        style = textStyle,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, height - paddingBottom + 8.dp.toPx())
                    )
                }
            }

            // Draw line chart
            val path = Path()
            val points = mutableListOf<Offset>()
            dataPoints.forEachIndexed { index, value ->
                val x = paddingLeft + (index * stepWidth)
                val y = height - paddingBottom - (value / maxData * chartHeight)
                points.add(Offset(x, y))
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw dots and vertical dashed line for Wednesday (index 2)
            points.forEachIndexed { index, offset ->
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = dotColor,
                    radius = 4.dp.toPx(),
                    center = offset,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = dotColor,
                    radius = 3.dp.toPx(),
                    center = offset
                )

                if (index == 2) { // Wed 14
                    drawLine(
                        color = dashColor,
                        start = Offset(offset.x, paddingTop),
                        end = Offset(offset.x, height - paddingBottom),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
        }
    }
}
