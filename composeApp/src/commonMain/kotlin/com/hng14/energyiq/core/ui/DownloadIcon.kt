package com.hng14.energyiq.core.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun DownloadIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Color(0xFF6B7280),
) {
    val icon = remember(tint) {
        ImageVector.Builder(
            name = "Download",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                fillAlpha = 0f,
                stroke = SolidColor(tint),
                strokeLineWidth = 1.3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(3.336f, 13.335f)
                verticalLineTo(14.165f)
                curveTo(3.336f, 15.546f, 4.455f, 16.665f, 5.836f, 16.665f)
                horizontalLineTo(14.169f)
                curveTo(15.55f, 16.665f, 16.669f, 15.546f, 16.669f, 14.165f)
                verticalLineTo(13.332f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                fillAlpha = 0f,
                stroke = SolidColor(tint),
                strokeLineWidth = 1.3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(10f, 3.75f)
                verticalLineTo(12.917f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                fillAlpha = 0f,
                stroke = SolidColor(tint),
                strokeLineWidth = 1.3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(12.919f, 10f)
                lineTo(10.003f, 12.917f)
                lineTo(7.086f, 10f)
            }
        }.build()
    }

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color.Unspecified,
    )
}
