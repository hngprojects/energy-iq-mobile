package com.hng14.energyiq.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CumulativeSavingsResponse(
    val success: Boolean,
    val message: String,
    val data: CumulativeSavingsData,
    val meta: ResponseMeta
)

@Serializable
data class CumulativeSavingsData(
    val lifetimeSavingsNgn: Double,
    val lifetimeEnergyKwh: Double,
    val lifetimeFuelSavedLitres: Double,
    val co2AvoidedKg: Double,
    val generatorHoursAvoided: Double,
    val totalSavingsToDateNgn: Double,
    val averageMonthlySavingsNgn: Double,
    val chart: List<CumulativeChartItem>,
    val meta: SavingsMeta
)

@Serializable
data class CumulativeChartItem(
    val month: String,
    val savingsNgn: Double
)
