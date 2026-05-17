package com.hng14.energyiq.features.onboarding.data

import com.hng14.energyiq.features.onboarding.data.remote.OnboardingApi

class OnboardingRepository(
    private val prefs: OnboardingPreferences,
    private val api: OnboardingApi,
) {

    suspend fun hasCompletedOnboarding(): Boolean = prefs.isComplete()

    suspend fun markOnboardingComplete() = prefs.markComplete()

    suspend fun fetchSupportedBrands(): List<String> = api.fetchSupportedBrands()
}
