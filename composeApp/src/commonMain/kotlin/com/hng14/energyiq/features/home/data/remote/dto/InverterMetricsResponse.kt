package com.hng14.energyiq.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class InverterMetricsResponse(
    val success: Boolean,
    val message: String,
    val data: InverterDashboardData? = null,
)

@Serializable
data class InverterDashboardData(
    val currentReadings: InverterReadings? = null,
    val dataAgeSeconds: Int? = null,
    val systemOffline: Boolean = false,
    val emptyData: Boolean = true,
    val nairaSavedToday: Double = 0.0,
    val nairaSavedThisMonth: Double = 0.0,
    val health: InverterHealth? = null,
    val sevenDayHistory: List<InverterHistoryItem> = emptyList(),
)

@Serializable
data class InverterReadings(
    // Backend can emit nulls when sensors are unavailable/transitioning.
    val solarKw: Double? = null,
    val batterySocPercent: Double? = null,
    val loadKw: Double? = null,
    val gridVoltageV: Double? = null,
    val batteryVoltageV: Double? = null,
    val recordedAt: String? = null,
)

@Serializable
data class InverterHealth(
    val status: String = "Healthy",
    val reason: String = "System performing optimally",
)

@Serializable
data class InverterHistoryItem(
    val date: String = "",
    val solarKwh: Double = 0.0,
    val avgBatterySocPercent: Double = 0.0,
    val avgLoadKw: Double = 0.0,
)
