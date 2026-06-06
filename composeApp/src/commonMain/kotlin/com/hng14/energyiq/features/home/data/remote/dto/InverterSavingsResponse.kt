package com.hng14.energyiq.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class InverterSavingsResponse(
    val success: Boolean,
    val message: String,
    val data: InverterSavingsData,
    val meta: ResponseMeta
)

@Serializable
data class InverterSavingsData(
    val startDate: String? = null,
    val endDate: String? = null,
    val spanDays: Int? = null,
    val granularity: String? = null,
    val period: String? = null,
    val date: String? = null,
    val results: SavingsResults,
    val summary: SavingsSummary,
    val chart: List<SavingsChartItem>,
    val meta: SavingsMeta
)

@Serializable
data class SavingsResults(
    val totalCostSavedNgn: Double,
    val generatorCostAvoidedNgn: Double,
    val fuelSavedLitres: Double,
    val co2AvoidedKg: Double,
    val breakdown: List<SavingsBreakdownItem>
)

@Serializable
data class SavingsBreakdownItem(
    val bucket: String,
    val activeHours: Double,
    val energyKwh: Double,
    val generatorCostSavedNgn: Double,
    val fuelSavedLitres: Double
)

@Serializable
data class SavingsSummary(
    val averageCostSavedNgn: Double? = null,
    val averageCostSavedPerBucketNgn: Double? = null,
    val totalEnergyConsumedKwh: Double,
    val totalActiveHours: Double
)

@Serializable
data class SavingsChartItem(
    val label: String,
    val savingsNgn: Double
)

@Serializable
data class SavingsMeta(
    val fuelType: String,
    val fuelPricePerLitreNgn: Double,
    val fuelPriceLastUpdated: String,
    val assumedGeneratorRatedPowerKw: Double,
    val assumedConsumptionRateLPerHr: Double
)

@Serializable
data class ResponseMeta(
    val timestamp: String
)
