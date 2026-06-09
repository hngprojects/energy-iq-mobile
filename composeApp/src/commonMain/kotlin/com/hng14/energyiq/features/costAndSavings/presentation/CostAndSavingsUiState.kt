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
    
    // Cumulative Tracker Data
    val lifetimeSavings: String = "₦ 0",
    val lifetimeSavingsTrend: String = "",
    val co2Avoided: String = "0 Tons",
    val genHoursAvoided: String = "0 hrs",
    val fuelSaved: String = "0 Litres",
    val totalSavingsToDate: String = "₦ 0",
    val totalSavingsTrend: String = "",
    val avgMonthlySavings: String = "₦ 0",
    val avgMonthlySavingsTrend: String = "",
    val efficiencyTrendLabel: String = "Stable efficiency trend",

    // Calculator Inputs
    val calculatorStep: Int = 1,
    val calculatorSelectedPeriod: String = "This Week",
    val calculatorCustomStartDate: String? = null,
    val calculatorCustomEndDate: String? = null,
    val calculatorCustomRangeLabel: String? = null,
    val isCalculatorStep3Editing: Boolean = false,
    val generatorType: String = "PMS",
    val pmsPrice: Double = 870.0,
    val pmsPriceString: String = "870",
    val fuelRate: Double = 0.0,
    val hoursUsed: Int = 0,
    val tariffBand: String = "",

    // Results Tab Specific Data
    val resultsMetaDescription: String = "",
    val resultsDailySavings: String = "₦ 0",
    val resultsWeeklySavings: String = "₦ 0",
    val resultsMonthlySavings: String = "₦ 0",
    val resultsGeneratorCostAvoided: String = "₦ 0",
    val resultsSavingsPercentage: String = "—",
    val resultsCo2Avoided: String = "0kg",
    val resultsTotalActiveHours: String = "0 hrs",
    val resultsEquivalentPower: String = "0 kWh",
    val resultsBreakdown: List<ResultsBreakdownRow> = emptyList(),
    val resultsFuelPriceImpact: String = "₦ 0"
)

data class ResultsBreakdownRow(
    val label: String,
    val before: String,
    val after: String
)
