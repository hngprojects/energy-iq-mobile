package com.hng14.energyiq.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class EnergyColors(
    val brandPrimary: Color,
    val brandPrimarySoft: Color,
    val appBackground: Color,
    val appBackgroundAlt: Color,
    val cardSurface: Color,
    val cardSurfaceMuted: Color,
    val success: Color,
    val successBackground: Color,
    val warning: Color,
    val warningBackground: Color,
    val danger: Color,
    val dangerBackground: Color,
    val chartSolar: Color,
    val chartDiesel: Color,
    val chartBattery: Color,
    val chartGrid: Color,
)

private val LightEnergyColors = EnergyColors(
    brandPrimary = EnergyPalette.Amber,
    brandPrimarySoft = EnergyPalette.AmberLight,
    appBackground = EnergyPalette.LightBackground,
    appBackgroundAlt = EnergyPalette.LightBackgroundAlt,
    cardSurface = Color.White,
    cardSurfaceMuted = EnergyPalette.AmberLight,
    success = EnergyPalette.BatteryGreen,
    successBackground = EnergyPalette.SuccessBadgeBackground,
    warning = EnergyPalette.BatteryMidLow,
    warningBackground = EnergyPalette.WarningBackground,
    danger = EnergyPalette.Danger,
    dangerBackground = EnergyPalette.DangerBackground,
    chartSolar = EnergyPalette.SolarYellow,
    chartDiesel = EnergyPalette.DieselOrange,
    chartBattery = EnergyPalette.BatteryGreen,
    chartGrid = EnergyPalette.GridBlueGrey,
)

private val DarkEnergyColors = EnergyColors(
    brandPrimary = EnergyPalette.Amber,
    brandPrimarySoft = EnergyPalette.CardSurfaceDark,
    appBackground = EnergyPalette.DarkBackground,
    appBackgroundAlt = EnergyPalette.SlateMid,
    cardSurface = EnergyPalette.CardSurfaceDark,
    cardSurfaceMuted = EnergyPalette.SlateMid,
    success = EnergyPalette.ChargeGreen,
    successBackground = EnergyPalette.BatteryGreen.copy(alpha = 0.24f),
    warning = EnergyPalette.SolarYellow,
    warningBackground = EnergyPalette.BatteryMidLow.copy(alpha = 0.24f),
    danger = Color(0xFFFF8A80),
    dangerBackground = EnergyPalette.Danger.copy(alpha = 0.20f),
    chartSolar = EnergyPalette.SolarYellow,
    chartDiesel = EnergyPalette.DieselOrange,
    chartBattery = EnergyPalette.ChargeGreen,
    chartGrid = EnergyPalette.GridBlueGrey,
)

internal val LocalEnergyColors = staticCompositionLocalOf { LightEnergyColors }

object EnergyTheme {
    val colors: EnergyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalEnergyColors.current
}

internal fun energyColors(darkTheme: Boolean): EnergyColors =
    if (darkTheme) DarkEnergyColors else LightEnergyColors
