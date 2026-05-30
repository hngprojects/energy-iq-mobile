package com.hng14.energyiq.features.onboarding.data

import com.hng14.energyiq.features.onboarding.data.remote.OnboardingApi

class OnboardingRepository(
    private val prefs: OnboardingPreferences,
    private val api: OnboardingApi,
) {

    suspend fun hasCompletedOnboarding(): Boolean = prefs.isComplete()

    suspend fun markOnboardingComplete() = prefs.markComplete()

    suspend fun fetchSupportedBrands(): List<String> = api.fetchSupportedBrands()

    suspend fun connectInverter(
        brand: String,
        victronAccessToken: String? = null,
        growattApiToken: String? = null,
        solarmanEmail: String? = null,
        solarmanPassword: String? = null,
        sandboxAccessToken: String? = null,
    ) {
        val response = api.connectInverter(
            request = com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterRequest(
                brand = brand,
                victronAccessToken = victronAccessToken,
                growattApiToken = growattApiToken,
                solarmanEmail = solarmanEmail,
                solarmanPassword = solarmanPassword,
                sandboxAccessToken = sandboxAccessToken,
            ),
        )
        prefs.saveInverterId(response.data.id)
    }

    suspend fun connectVictron(victronAccessToken: String) {
        connectInverter(brand = "VICTRON", victronAccessToken = victronAccessToken)
    }

    suspend fun connectSandbox(sandboxAccessToken: String) {
        connectInverter(brand = "SANDBOX", sandboxAccessToken = sandboxAccessToken)
    }

    suspend fun getSavedInverterId(): String? = prefs.getInverterId()

    suspend fun saveInverterId(id: String) = prefs.saveInverterId(id)
}
