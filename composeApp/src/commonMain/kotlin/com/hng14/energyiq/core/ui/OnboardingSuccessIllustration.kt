package com.hng14.energyiq.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun OnboardingSuccessIllustration(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
