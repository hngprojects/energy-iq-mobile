package com.hng14.energyiq.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// Light scheme
val LightBackground = EnergyPalette.LightBackground
val LightOnBackground = EnergyPalette.DarkBackground
val LightSurface = EnergyPalette.LightBackgroundAlt
val LightOnSurface = EnergyPalette.DarkBackground
val LightSurfaceVariant = EnergyPalette.AmberLight
val LightOnSurfaceVariant = EnergyPalette.Grey
val LightOutline = EnergyPalette.Grey

// Dark scheme
val DarkBackground = EnergyPalette.DarkBackground
val DarkOnBackground = EnergyPalette.LightBackgroundAlt
val DarkSurface = EnergyPalette.SlateMid
val DarkOnSurface = EnergyPalette.LightBackgroundAlt
val DarkSurfaceVariant = EnergyPalette.CardSurfaceDark
val DarkOnSurfaceVariant = EnergyPalette.Grey
val DarkOutline = EnergyPalette.Grey


val LightColorScheme = lightColorScheme(
    primary = EnergyPalette.Amber,
    onPrimary = EnergyPalette.DarkBackground,
    primaryContainer = EnergyPalette.AmberLight,
    onPrimaryContainer = EnergyPalette.DarkBackground,
    secondary = EnergyPalette.ChargeGreen,
    onSecondary = EnergyPalette.DarkBackground,
    secondaryContainer = EnergyPalette.SuccessBadgeBackground,
    onSecondaryContainer = EnergyPalette.BatteryGreen,
    tertiary = EnergyPalette.Coral,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = EnergyPalette.DieselOrange.copy(alpha = 0.20f),
    onTertiaryContainer = EnergyPalette.Coral,
    error = EnergyPalette.Danger,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = EnergyPalette.DangerBackground,
    onErrorContainer = EnergyPalette.Danger,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
)

val DarkColorScheme = darkColorScheme(
    primary = EnergyPalette.Amber,
    onPrimary = EnergyPalette.DarkBackground,
    primaryContainer = EnergyPalette.CardSurfaceDark,
    onPrimaryContainer = EnergyPalette.LightBackgroundAlt,
    secondary = EnergyPalette.ChargeGreen,
    onSecondary = EnergyPalette.DarkBackground,
    secondaryContainer = EnergyPalette.BatteryGreen.copy(alpha = 0.24f),
    onSecondaryContainer = EnergyPalette.ChargeGreen,
    tertiary = EnergyPalette.Coral,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = EnergyPalette.Coral.copy(alpha = 0.20f),
    onTertiaryContainer = EnergyPalette.DieselOrange,
    error = androidx.compose.ui.graphics.Color(0xFFFF8A80),
    onError = EnergyPalette.DarkBackground,
    errorContainer = EnergyPalette.Danger.copy(alpha = 0.20f),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
)
