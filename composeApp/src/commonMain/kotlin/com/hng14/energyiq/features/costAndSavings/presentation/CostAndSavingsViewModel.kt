package com.hng14.energyiq.features.costAndSavings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.costAndSavings.data.CostAndSavingsRepository
import com.hng14.energyiq.features.profile.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CostAndSavingsViewModel(
    private val repository: CostAndSavingsRepository,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CostAndSavingsUiState())
    val uiState: StateFlow<CostAndSavingsUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun onTimeframeSelected(timeframe: String) {
        _uiState.update { it.copy(selectedTimeframe = timeframe) }
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                coroutineScope {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val todayStr = now.date.toString()

                    val savingsJob = async {
                        repository.getInverterSavings(
                            period = _uiState.value.selectedTimeframe.lowercase(),
                            date = todayStr
                        )
                    }

                    val cumulativeJob = async {
                        repository.getCumulativeSavings()
                    }

                    val savingsResponse = try { savingsJob.await() } catch (e: Exception) { null }
                    val cumulativeResponse = try { cumulativeJob.await() } catch (e: Exception) { null }

                    if (savingsResponse?.success == true) {
                        val data = savingsResponse.data
                        updateSavingsData(data)
                    }

                    if (cumulativeResponse?.success == true) {
                        val cData = cumulativeResponse.data
                        _uiState.update {
                            it.copy(
                                lifetimeSavings = "₦ ${formatLargeNumber(cData.lifetimeSavingsNgn ?: 0.0)}",
                                lifetimeSavingsTrend = "+12%",
                                co2Avoided = "${(((cData.co2AvoidedKg ?: 0.0) / 1000.0) * 10).toInt() / 10.0} Tons",
                                genHoursAvoided = "${(cData.generatorHoursAvoided ?: 0.0).toInt()} hrs",
                                fuelSaved = "${(cData.lifetimeFuelSavedLitres ?: 0.0).toInt()} Litres",
                                totalSavingsToDate = "₦ ${formatCurrency(cData.totalSavingsToDateNgn)}",
                                totalSavingsTrend = "+14.2%",
                                avgMonthlySavings = "₦ ${formatCurrency(cData.averageMonthlySavingsNgn)}",
                                avgMonthlySavingsTrend = "+2.4%",
                            )
                        }
                    }
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateSavingsData(data: com.hng14.energyiq.features.home.data.remote.dto.InverterSavingsData) {
        val totalCostSaved = data.results.totalCostSavedNgn ?: 0.0
        val generatorCostAvoided = data.results.generatorCostAvoidedNgn ?: 0.0
        val co2Avoided = data.results.co2AvoidedKg ?: 0.0
        
        val fuelPrice = data.meta.fuelPricePerLitreNgn ?: 870.0
        val rate = data.meta.assumedConsumptionRateLPerHr ?: 1.3
        val totalEnergy = data.summary.totalEnergyConsumedKwh ?: 0.0
        val hoursBefore = data.summary.totalActiveHours ?: 0.0

        _uiState.update {
            it.copy(
                pmsPrice = fuelPrice,
                pmsPriceString = fuelPrice.toInt().toString(),
                generatorType = data.meta.fuelType ?: "PMS",
                totalSaved = "₦ ${formatCurrency(totalCostSaved)}",
                energyConsumed = "$totalEnergy kWh",
                generationToday = "${data.summary.totalEnergyGeneratedKwh ?: 0.0} kWh",
                
                resultsMetaDescription = "${data.meta.fuelType ?: "Petrol"} ${rate} L/hr · ${hoursBefore.toInt()} hrs total · Band A · Lagos",
                resultsDailySavings = "₦${formatCurrency(totalCostSaved)}",
                resultsWeeklySavings = "₦${formatCurrency(totalCostSaved)}",
                resultsMonthlySavings = "₦${formatCurrency(totalCostSaved)}",
                resultsGeneratorCostAvoided = "₦${formatCurrency(generatorCostAvoided)}",
                resultsSavingsPercentage = "${(data.summary.solarCoveragePercent ?: 0.0).toInt()}%",
                resultsCo2Avoided = "$co2Avoided kg", // Exact value in KG
                resultsTotalActiveHours = "${data.summary.totalActiveHours ?: 0.0} hrs",
                resultsEquivalentPower = "${data.summary.totalEnergyGeneratedKwh ?: 0.0} kWh",
                resultsBreakdown = data.results.breakdown.take(4).map { item ->
                    ResultsBreakdownRow(
                        label = item.bucket.replaceFirstChar { char -> char.uppercase() },
                        before = "₦${formatCurrency(item.energyKwh?.let { kwh -> (kwh * 0.52 * fuelPrice) })}", 
                        after = "₦${formatCurrency(item.generatorCostSavedNgn)}"
                    )
                },
                resultsFuelPriceImpact = "₦${formatCurrency(totalCostSaved * 0.4)}"
            )
        }
    }

    // Calculator Actions
    fun onCalculatorStepChanged(step: Int) {
        _uiState.update { it.copy(calculatorStep = step) }
    }

    fun onToggleStep3Editing() {
        _uiState.update { it.copy(isCalculatorStep3Editing = !it.isCalculatorStep3Editing) }
    }

    fun onCalculatorPeriodSelected(period: String) {
        _uiState.update { it.copy(calculatorSelectedPeriod = period) }
        if (_uiState.value.calculatorStep == 3) {
             updateFuelPriceAndProceed(proceedToStep3 = false)
        }
    }

    fun onCalculatorCustomDateRangeSelected(startDate: String, endDate: String, label: String) {
        _uiState.update {
            it.copy(
                calculatorSelectedPeriod = "Custom Range",
                calculatorCustomStartDate = startDate,
                calculatorCustomEndDate = endDate,
                calculatorCustomRangeLabel = label
            )
        }
        if (_uiState.value.calculatorStep == 3) {
            updateFuelPriceAndProceed(proceedToStep3 = false)
        }
    }

    fun onGeneratorTypeChanged(type: String) {
        _uiState.update { it.copy(generatorType = type) }
        if (_uiState.value.calculatorStep == 3) {
            updateFuelPriceAndProceed(proceedToStep3 = false)
        }
    }

    fun onPmsPriceChanged(price: Double) {
        _uiState.update { it.copy(pmsPrice = price, pmsPriceString = price.toInt().toString()) }
        if (_uiState.value.calculatorStep == 3) {
            updateFuelPriceAndProceed(proceedToStep3 = false)
        }
    }

    fun onPmsPriceStringChanged(priceStr: String) {
        _uiState.update { it.copy(pmsPriceString = priceStr) }
        priceStr.toDoubleOrNull()?.let { price ->
            _uiState.update { it.copy(pmsPrice = price) }
            if (_uiState.value.calculatorStep == 3) {
                updateFuelPriceAndProceed(proceedToStep3 = false)
            }
        }
    }

    fun onCalculatorContinue() {
        val currentState = _uiState.value
        if (currentState.calculatorStep == 1) {
            _uiState.update { it.copy(calculatorStep = 2) }
        } else if (currentState.calculatorStep == 2) {
            updateFuelPriceAndProceed()
        }
    }

    private fun updateFuelPriceAndProceed(proceedToStep3: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = runCatching {
                val user = authRepository.getCurrentUser()
                val body = buildJsonObject {
                    put("firstName", user?.name?.substringBefore(" ") ?: "User")
                    
                    val lastName = user?.name?.substringAfter(" ", "") ?: ""
                    if (lastName.isNotBlank()) put("lastName", lastName)
                    
                    val profileUrl = user?.profileUrl
                    if (!profileUrl.isNullOrBlank()) {
                        put("profileUrl", profileUrl)
                    }
                    
                    put("businessName", user?.businessName ?: "My Business")
                    put("businessType", user?.businessType ?: "Other")
                    put("state", user?.state ?: "Lagos")
                    put("city", user?.city ?: "Lagos")
                    put("aiLanguage", user?.aiLanguage ?: "English")
                    put("customFuelPriceNaira", _uiState.value.pmsPrice)
                    put("generatorRatedPowerKw", 2.5) 
                    put("generatorFuelType", _uiState.value.generatorType)
                    put("generatorAverageDailyRuntimeHours", 8)
                }
                
                profileRepository.updatePersonalSettingsRaw(body)
            }
            
            if (result.isSuccess) {
                _uiState.update { it.copy(
                    calculatorStep = if (proceedToStep3) 3 else it.calculatorStep,
                    isLoading = false
                ) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                if (proceedToStep3) _uiState.update { it.copy(calculatorStep = 3) }
            }
        }
    }

    fun onCalculate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val todayStr = now.date.toString()
                
                val period = when(_uiState.value.calculatorSelectedPeriod) {
                    "This Week" -> "weekly"
                    "This Month" -> "monthly"
                    "Last Month" -> "monthly"
                    "Custom Range" -> "daily"
                    else -> "daily"
                }

                val endDateObj = now.date
                var startDateStr: String?
                var endDateStr: String?

                when(_uiState.value.calculatorSelectedPeriod) {
                    "This Week" -> {
                        startDateStr = endDateObj.minus(7, DateTimeUnit.DAY).toString()
                        endDateStr = endDateObj.toString()
                    }
                    "This Month" -> {
                        startDateStr = endDateObj.minus(endDateObj.day - 1, DateTimeUnit.DAY).toString()
                        endDateStr = endDateObj.toString()
                    }
                    "Last Month" -> {
                        val firstOfThisMonth = endDateObj.minus(endDateObj.day - 1, DateTimeUnit.DAY)
                        val lastOfLastMonth = firstOfThisMonth.minus(1, DateTimeUnit.DAY)
                        startDateStr = lastOfLastMonth.minus(lastOfLastMonth.day - 1, DateTimeUnit.DAY).toString()
                        endDateStr = lastOfLastMonth.toString()
                    }
                    "Custom Range" -> {
                        startDateStr = _uiState.value.calculatorCustomStartDate
                        endDateStr = _uiState.value.calculatorCustomEndDate
                    }
                    else -> {
                        startDateStr = endDateObj.toString()
                        endDateStr = endDateObj.toString()
                    }
                }

                val response = repository.getInverterSavings(
                    period = period,
                    date = endDateStr ?: todayStr,
                    startDate = startDateStr,
                    endDate = endDateStr
                )

                if (response.success) {
                    updateSavingsData(response.data)
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun formatLargeNumber(amount: Double): String {
        return if (amount >= 1_000_000) {
            "${(amount / 1_000_000.0 * 10).toInt() / 10.0}M"
        } else if (amount >= 1_000) {
            "${(amount / 1_000.0).toInt()}k"
        } else {
            amount.toInt().toString()
        }
    }

    private fun formatCurrency(amount: Double?): String {
        if (amount == null) return "0"
        return amount.toInt().toString().reversed().chunked(3).joinToString(",").reversed()
    }
}
