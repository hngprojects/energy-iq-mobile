package com.hng14.energyiq.features.costAndSavings.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
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
    val axisColor = Color(0xFFF1F1F1)
    val textColor = Color(0xFFB6B8BD)
    val lineColor = Color(0xFF16A34A)
    val labelColor = Color(0xFF16A34A)
    val dashColor = Color(0xFFE5E7EB)

    val axisTextStyle = TextStyle(
        color = textColor,
        fontSize = 12.sp,
        fontFamily = fontFamily
    )
    
    val pointLabelTextStyle = TextStyle(
        color = labelColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily
    )

    Box(modifier = modifier.fillMaxWidth().height(250.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 45.dp.toPx()
            val paddingBottom = 30.dp.toPx()
            val paddingTop = 40.dp.toPx() // More top padding for labels
            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom - paddingTop

            val maxData = 35000f
            val stepsY = 5
            val stepHeight = chartHeight / (stepsY - 1)

            // Draw Y-axis labels and horizontal grid lines
            for (i in 0 until stepsY) {
                val y = height - paddingBottom - (i * stepHeight)
                val value = (i * 7000)
                val label = "₦${value / 1000}k"
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = axisTextStyle,
                    topLeft = Offset(0f, y - 10.dp.toPx())
                )
                
                drawLine(
                    color = axisColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw X-axis labels
            val stepWidth = chartWidth / (dataPoints.size - 1)
            val sparseLabels = listOf(0, 2, 4, 6)
            sparseLabels.forEach { index ->
                if (index < labels.size) {
                    val x = paddingLeft + (index * stepWidth)
                    val label = labels[index]
                    val textLayoutResult = textMeasurer.measure(label, axisTextStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        style = axisTextStyle,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, height - paddingBottom + 8.dp.toPx())
                    )
                }
            }

            // Calculate points
            val points = dataPoints.mapIndexed { index, value ->
                val x = paddingLeft + (index * stepWidth)
                val y = height - paddingBottom - (value / maxData * chartHeight)
                Offset(x, y)
            }

            // Draw smooth path
            if (points.isNotEmpty()) {
                val path = Path()
                path.moveTo(points[0].x, points[0].y)
                
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val controlX = (p0.x + p1.x) / 2
                    path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Draw labels and highlight
            points.forEachIndexed { index, offset ->
                val value = dataPoints[index]
                val labelText = "₦${(value / 1000).toInt()}k"
                val textLayoutResult = textMeasurer.measure(labelText, pointLabelTextStyle)
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = labelText,
                    style = pointLabelTextStyle,
                    topLeft = Offset(offset.x - textLayoutResult.size.width / 2, offset.y - 20.dp.toPx())
                )

                if (index == 2) { // Highlight Wed 14
                    drawLine(
                        color = dashColor,
                        start = Offset(offset.x, 10.dp.toPx()),
                        end = Offset(offset.x, height - paddingBottom),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }
        }
    }
}
