package com.hng14.energyiq.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun ChatBotVectorIcon(
    modifier: Modifier,
    contentDescription: String?,
) {
    Icon(
        imageVector = Icons.Outlined.SmartToy,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color.White,
    )
}
