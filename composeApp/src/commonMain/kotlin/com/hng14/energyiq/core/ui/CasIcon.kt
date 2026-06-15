package com.hng14.energyiq.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
expect fun CasIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color? = null,
)
