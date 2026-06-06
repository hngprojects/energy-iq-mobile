package com.hng14.energyiq.features.costAndSavings.presentation

data class CostAndSavingsUiState(
    val userName: String? = null,
    val isLoading: Boolean = false,
    val selectedTimeframe: String = "Daily",
    val totalSaved: String = "₦ 0",
    val totalSavedTrend: String = "",
    val generatorCostAvoided: String = "₦ 0",
    val generatorTrend: String = "",
    val energyConsumed: String = "0 kWh",
    val energyTrend: String = "",
    val generationToday: String = "0 kWh",
    val generationRemaining: String = "",
    // Chart Data
    val chartData: List<Float> = listOf(15000f, 15000f, 10000f, 20000f, 22000f, 15000f, 27000f),
    val chartLabels: List<String> = listOf("Mon 12", "Tue 13", "Wed 14", "Thu 15", "Fri 16", "Sat 17", "Sun 18"),
    // Calculator Inputs
    val pmsPrice: Double = 0.0,
    val fuelRate: Double = 0.0,
    val hoursUsed: Int = 0,
    val tariffBand: String = ""
)