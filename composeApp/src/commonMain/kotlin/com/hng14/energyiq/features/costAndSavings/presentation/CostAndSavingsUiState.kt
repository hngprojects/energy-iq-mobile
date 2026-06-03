package com.hng14.energyiq.features.costAndSavings.presentation

data class CostAndSavingsUiState(
    val userName: String? = null,
    val selectedTimeframe: String = "Weekly",
    val totalSaved: String = "₦28,400",
    val totalSavedTrend: String = "+₦4,200 vs last week",
    val generatorCostAvoided: String = "₦52,400",
    val generatorTrend: String = "12% vs last week",
    val energyConsumed: String = "38.7 kwh",
    val energyTrend: String = "8% vs last week",
    val generationToday: String = "78%",
    val generationRemaining: String = "3h 45m remaining",
    // Calculator Inputs
    val pmsPrice: Double = 0.0,
    val fuelRate: Double = 0.0,
    val hoursUsed: Int = 0,
    val tariffBand: String = ""
)