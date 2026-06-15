package com.hng14.energyiq.core.util


import androidx.compose.runtime.Composable
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

@Composable
actual fun isReduceMotionEnabled(): Boolean {
    return UIAccessibilityIsReduceMotionEnabled()
}