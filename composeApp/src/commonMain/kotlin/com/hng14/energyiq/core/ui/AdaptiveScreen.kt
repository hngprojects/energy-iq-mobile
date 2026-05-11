package com.hng14.energyiq.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class WidthTier {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

data class AdaptiveScreenSpec(
    val tier: WidthTier,
    val contentMaxWidth: Dp,
    val headlineSize: androidx.compose.ui.unit.TextUnit,
    val bodySize: androidx.compose.ui.unit.TextUnit,
    val buttonHeight: Dp,
    val inverterCardWidth: Dp,
    val inverterGridColumns: Int,
)

fun adaptiveScreenSpec(screenWidth: Dp): AdaptiveScreenSpec {
    return when {
        screenWidth >= 840.dp -> AdaptiveScreenSpec(
            tier = WidthTier.EXPANDED,
            contentMaxWidth = 620.dp,
            headlineSize = 28.sp,
            bodySize = 16.sp,
            buttonHeight = 58.dp,
            inverterCardWidth = 156.dp,
            inverterGridColumns = 3,
        )

        screenWidth >= 600.dp -> AdaptiveScreenSpec(
            tier = WidthTier.MEDIUM,
            contentMaxWidth = 520.dp,
            headlineSize = 24.sp,
            bodySize = 15.sp,
            buttonHeight = 56.dp,
            inverterCardWidth = 144.dp,
            inverterGridColumns = 2,
        )

        else -> AdaptiveScreenSpec(
            tier = WidthTier.COMPACT,
            contentMaxWidth = 460.dp,
            headlineSize = 22.sp,
            bodySize = 14.sp,
            buttonHeight = 54.dp,
            inverterCardWidth = 130.dp,
            inverterGridColumns = 2,
        )
    }
}

val LocalAdaptiveScreenSpec = compositionLocalOf {
    adaptiveScreenSpec(360.dp)
}
