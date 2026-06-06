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
    
    // Cumulative Tracker Data
    val lifetimeSavings: String = "₦ 0",
    val lifetimeSavingsTrend: String = "",
    val co2Avoided: String = "0 Tons",
    val genHoursAvoided: String = "0 hrs",
    val fuelSaved: String = "0 Litres",
    val cumulativeActualSavings: List<Float> = emptyList(),
    val cumulativeGridProjection: List<Float> = emptyList(),
    val cumulativeChartLabels: List<String> = emptyList(),
    val totalSavingsToDate: String = "₦ 0",
    val totalSavingsTrend: String = "",
    val avgMonthlySavings: String = "₦ 0",
    val avgMonthlySavingsTrend: String = "",
    val efficiencyTrendLabel: String = "Stable efficiency trend",

    // Calculator Inputs
    val pmsPrice: Double = 0.0,
    val fuelRate: Double = 0.0,
    val hoursUsed: Int = 0,
    val tariffBand: String = ""
)