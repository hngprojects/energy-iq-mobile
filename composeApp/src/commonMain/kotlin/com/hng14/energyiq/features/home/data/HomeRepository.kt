package com.hng14.energyiq.features.home.data

import com.hng14.energyiq.features.home.data.remote.InverterApi
import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
import com.hng14.energyiq.core.storage.PreferenceStore
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import kotlinx.serialization.json.Json

class HomeRepository(
    private val inverterApi: InverterApi,
    private val onboardingRepository: OnboardingRepository,
    private val store: PreferenceStore,
    private val json: Json,
    private val authPreferences: AuthPreferences
) {
    private companion object {
        const val CACHE_KEY = "home_dashboard_cache"
    }

    suspend fun getInverterDashboard(): com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse? {
        val inverterId = onboardingRepository.getSavedInverterId() ?: return null
        return try {
            val response = inverterApi.fetchInverterDashboard(inverterId)

            // Generate a unique key for the current user
            val scopedKey = authPreferences.getUserScopedKey(CACHE_KEY)

            // Save to manual cache on success
            store.put(scopedKey, json.encodeToString(com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse.serializer(), response))
            response
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getCachedInverterDashboard(): com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse? {
        return try {
            val scopedKey = authPreferences.getUserScopedKey(CACHE_KEY)
            val cachedJson = store.get(scopedKey) ?: return null
            json.decodeFromString<com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse>(
                cachedJson
            )
        } catch (e: Exception) {
            null
        }
    }
}
