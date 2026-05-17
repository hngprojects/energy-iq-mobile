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
fun TransactionHistoryIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Color(0xFF6B7280),
) {
    val icon = remember(tint) {
        ImageVector.Builder(
            name = "TransactionHistory",
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
                moveTo(15.833f, 8.751f)
                verticalLineTo(8.335f)
                curveTo(15.833f, 5.192f, 15.833f, 3.621f, 14.857f, 2.644f)
                curveTo(13.881f, 1.668f, 12.309f, 1.668f, 9.167f, 1.668f)
                curveTo(6.024f, 1.668f, 4.453f, 1.668f, 3.476f, 2.644f)
                curveTo(2.5f, 3.621f, 2.5f, 5.192f, 2.5f, 8.335f)
                verticalLineTo(12.085f)
                curveTo(2.5f, 14.824f, 2.5f, 16.194f, 3.257f, 17.116f)
                curveTo(3.395f, 17.285f, 3.55f, 17.44f, 3.719f, 17.578f)
                curveTo(4.641f, 18.335f, 6.01f, 18.335f, 8.75f, 18.335f)
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
                moveTo(5.836f, 5.832f)
                horizontalLineTo(12.503f)
                moveTo(5.836f, 9.165f)
                horizontalLineTo(9.169f)
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
                moveTo(15f, 15.415f)
                lineTo(13.75f, 14.957f)
                verticalLineTo(12.915f)
                moveTo(10f, 14.582f)
                curveTo(10f, 16.653f, 11.679f, 18.332f, 13.75f, 18.332f)
                curveTo(15.821f, 18.332f, 17.5f, 16.653f, 17.5f, 14.582f)
                curveTo(17.5f, 12.511f, 15.821f, 10.832f, 13.75f, 10.832f)
                curveTo(11.679f, 10.832f, 10f, 12.511f, 10f, 14.582f)
                close()
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
