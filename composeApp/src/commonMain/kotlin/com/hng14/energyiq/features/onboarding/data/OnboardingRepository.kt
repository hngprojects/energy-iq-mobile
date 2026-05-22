package com.hng14.energyiq.features.onboarding.data

import com.hng14.energyiq.features.onboarding.data.remote.OnboardingApi

class OnboardingRepository(
    private val prefs: OnboardingPreferences,
    private val api: OnboardingApi,
) {

    suspend fun hasCompletedOnboarding(): Boolean = prefs.isComplete()

    suspend fun markOnboardingComplete() = prefs.markComplete()

    suspend fun fetchSupportedBrands(): List<String> = api.fetchSupportedBrands()

    suspend fun connectVictron(victronAccessToken: String) {
        api.connectInverter(
            request = com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterRequest(
                brand = "VICTRON",
                victronAccessToken = victronAccessToken,
            ),
        )
    }

    suspend fun connectSandbox(sandboxAccessToken: String) {
        api.connectInverter(
            request = com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterRequest(
                brand = "SANDBOX",
                sandboxAccessToken = sandboxAccessToken,
            ),
        )
    }
}
