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
    val totalCostSavedNgn: Double? = null,
    val generatorCostAvoidedNgn: Double? = null,
    val fuelSavedLitres: Double? = null,
    val co2AvoidedKg: Double? = null,
    val breakdown: List<SavingsBreakdownItem>
)

@Serializable
data class SavingsBreakdownItem(
    val bucket: String,
    val activeHours: Double? = null,
    val energyKwh: Double? = null,
    val solarKwh: Double? = null,
    val generatorCostSavedNgn: Double? = null,
    val fuelSavedLitres: Double? = null
)

@Serializable
data class SavingsSummary(
    val totalCostSavedNgn: Double? = null,
    val averageCostSavedNgn: Double? = null,
    val averageCostSavedPerBucketNgn: Double? = null,
    val totalEnergyConsumedKwh: Double? = null,
    val totalEnergyGeneratedKwh: Double? = null,
    val solarCoveragePercent: Double? = null,
    val totalActiveHours: Double? = null
)

@Serializable
data class SavingsChartItem(
    val label: String,
    val savingsNgn: Double? = null
)

@Serializable
data class SavingsMeta(
    val fuelType: String? = null,
    val fuelPricePerLitreNgn: Double? = null,
    val fuelPriceLastUpdated: String? = null,
    val assumedGeneratorRatedPowerKw: Double? = null,
    val assumedConsumptionRateLPerHr: Double? = null
)

@Serializable
data class ResponseMeta(
    val timestamp: String
)
