package com.hng14.energyiq.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InsightOutlinedCard(
    modifier: Modifier = Modifier,
    shapeSize: Dp = 12.dp,
    paddingValues: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(shapeSize),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFECEEF1)),
    ) {
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}

@Composable
fun InsightStatCard(
    title: String,
    value: String,
    subtitle: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
    hideDot: Boolean = false,
    titleLeading: (@Composable () -> Unit)? = null,
    titleFontSize: TextUnit = 14.sp,
    titleLineHeight: TextUnit = 21.sp,
    valueFontSize: TextUnit = 28.sp,
    percentChange : String = "",
) {
    InsightOutlinedCard(
        modifier = modifier,
        shapeSize = 10.dp,
        paddingValues = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
    ) {
        androidx.compose.foundation.layout.Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                titleLeading?.let {
                    it()
                    Spacer(modifier = Modifier.width(10.dp))
                }
                if (!hideDot) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(dotColor, CircleShape),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = titleLineHeight,
                    ),
                    color = Color(0xFF666666),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = valueFontSize,
                    lineHeight = 33.6.sp,
                    letterSpacing = (-0.28).sp,
                ),
                color = Color(0xFF080C13),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = percentChange,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF16A34A),
                        letterSpacing = (-0.28).sp,)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                    color = Color(0xFF666666),
                )
            }
        }
    }
}

@Composable
fun InsightStatusChip(
    label: String,
    background: Color,
    foreground: Color,
    dot: Color,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(dot, CircleShape),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                ),
                color = foreground,
                textAlign = TextAlign.Center,
            )
        }
    }
}

enum class InsightButtonVariant {
    Primary,
    Secondary,
}

@Composable
fun InsightSmallButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: InsightButtonVariant = InsightButtonVariant.Primary,
    fontFamily: FontFamily? = null,
) {
    val background = when (variant) {
        InsightButtonVariant.Primary -> if (enabled) Color(0xFF111827) else Color(0xFFF3F4F6)
        InsightButtonVariant.Secondary -> Color(0xFFF3F4F6)
    }
    val foreground = when (variant) {
        InsightButtonVariant.Primary -> if (enabled) Color.White else Color(0xFF9CA3AF)
        InsightButtonVariant.Secondary -> if (enabled) Color(0xFF111827) else Color(0xFF9CA3AF)
    }
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                color = foreground,
                textAlign = TextAlign.Center,
            )
        }
    }
}
