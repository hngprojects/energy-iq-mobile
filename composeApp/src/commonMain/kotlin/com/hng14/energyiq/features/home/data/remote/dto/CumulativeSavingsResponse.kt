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
    val lifetimeSavingsNgn: Double? = null,
    val lifetimeEnergyConsumedKwh: Double? = null,
    val lifetimeEnergyGeneratedKwh: Double? = null,
    val lifetimeFuelSavedLitres: Double? = null,
    val co2AvoidedKg: Double? = null,
    val generatorHoursAvoided: Double? = null,
    val totalSavingsToDateNgn: Double? = null,
    val averageMonthlySavingsNgn: Double? = null,
    val chart: List<CumulativeChartItem> = emptyList(),
    val meta: SavingsMeta? = null
)

@Serializable
data class CumulativeChartItem(
    val month: String,
    val savingsNgn: Double? = null
)
