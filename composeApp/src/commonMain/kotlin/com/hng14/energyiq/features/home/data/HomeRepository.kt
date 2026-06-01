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
        const val SELECTED_INVERTER_KEY = "home_selected_inverter_id"
    }

    // We intentionally do not rely on a persisted inverterId to avoid stale/mismatched data
    // when users switch accounts or their active inverter changes server-side.
    // We still cache the resolved inverterId for the current app session to avoid calling
    // the "user inverters" endpoint on every poll/refresh.
    private var sessionUserId: String? = null
    private var sessionInverterId: String? = null

    data class DashboardFetch(
        val userId: String,
        val inverterId: String,
        val response: com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse,
    )

    fun peekSessionInverterId(): String? = sessionInverterId

    suspend fun getSelectedInverterId(): String? {
        val scopedKey = authPreferences.getUserScopedKey(SELECTED_INVERTER_KEY)
        return store.get(scopedKey)
    }

    suspend fun setSelectedInverterId(inverterId: String?) {
        val scopedKey = authPreferences.getUserScopedKey(SELECTED_INVERTER_KEY)
        store.put(scopedKey, inverterId)
        sessionInverterId = inverterId
    }

    suspend fun getInverterDashboard(): DashboardFetch? {
        val userId = authPreferences.getUserId() ?: return null

        // HomeRepository is typically a singleton; make the in-memory cache user-scoped
        // so switching accounts doesn't reuse the previous user's inverterId.
        if (sessionUserId != userId) {
            sessionUserId = userId
            sessionInverterId = null
        }

        var inverterId = sessionInverterId

        // If the session doesn't have an inverterId yet, attempt to restore the user's last selection.
        if (inverterId.isNullOrBlank()) {
            inverterId = getSelectedInverterId()
            if (!inverterId.isNullOrBlank()) {
                sessionInverterId = inverterId
            }
        }

        if (inverterId.isNullOrBlank()) {
            // Always resolve inverterId from server for the current user, then cache in-session.
            val userInverters = inverterApi.fetchUserInverters(userId)
            val selected = userInverters.data.firstOrNull { it.isActive } ?: userInverters.data.firstOrNull()
            inverterId = selected?.id ?: return null

            sessionInverterId = inverterId
            // Optional persistence (useful for offline/first paint), but we do NOT read from it.
            onboardingRepository.saveInverterId(inverterId)

            // Persist the selection so we can restore on next app start.
            setSelectedInverterId(inverterId)
        }

        return try {
            val response = inverterApi.fetchInverterDashboard(inverterId)

            // Generate a unique key for the current user
            val scopedKey = authPreferences.getUserScopedKey(CACHE_KEY)

            // Save to manual cache on success
            store.put(scopedKey, json.encodeToString(com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse.serializer(), response))
            DashboardFetch(userId = userId, inverterId = inverterId, response = response)
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

    suspend fun getUserInverters(): List<com.hng14.energyiq.features.home.data.remote.dto.InverterDto> {
        val userId = authPreferences.getUserId() ?: return emptyList()
        return inverterApi.fetchUserInverters(userId).data
    }
}
