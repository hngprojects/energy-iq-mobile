package com.hng14.energyiq.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun BulbVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    Icon(
        imageVector = Icons.Outlined.Lightbulb,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color(0xFF1F2937),
    )
}
