package com.hng14.energyiq.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Converts a [Dp] value to a non-scaling [TextUnit] in sp.
 * This is useful for graphical elements that contain text (like avatar initials or badges)
 * where the text size must remain constant relative to its fixed container size,
 * ignoring system-wide font scaling settings.
 */
@Composable
fun Dp.toNonScaledSp(): TextUnit {
    val fontScale = LocalDensity.current.fontScale
    return (this.value / fontScale).sp
}
