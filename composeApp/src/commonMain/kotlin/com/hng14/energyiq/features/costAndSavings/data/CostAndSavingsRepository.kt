package com.hng14.energyiq.features.costAndSavings.data

import com.hng14.energyiq.features.home.data.remote.InverterApi
import com.hng14.energyiq.features.home.data.remote.dto.InverterSavingsResponse
import com.hng14.energyiq.features.home.data.remote.dto.CumulativeSavingsResponse
import com.hng14.energyiq.features.home.data.HomeRepository

class CostAndSavingsRepository(
    private val inverterApi: InverterApi,
    private val homeRepository: HomeRepository
) {
    suspend fun getInverterSavings(
        period: String,
        date: String,
        startDate: String? = null,
        endDate: String? = null
    ): InverterSavingsResponse {
        val inverterId = homeRepository.getSelectedInverterId() 
            ?: homeRepository.getInverterDashboard()?.inverterId
            ?: throw Exception("No active inverter found")
            
        return inverterApi.fetchInverterSavings(
            inverterId = inverterId,
            period = period,
            date = date,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getCumulativeSavings(): CumulativeSavingsResponse {
        val inverterId = homeRepository.getSelectedInverterId()
            ?: homeRepository.getInverterDashboard()?.inverterId
            ?: throw Exception("No active inverter found")

        return inverterApi.fetchCumulativeSavings(inverterId)
    }
}
