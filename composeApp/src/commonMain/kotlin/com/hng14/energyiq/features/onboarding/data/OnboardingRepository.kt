package com.hng14.energyiq.features.onboarding.data

class OnboardingRepository(
    private val prefs: OnboardingPreferences,
) {

    suspend fun hasCompletedOnboarding(): Boolean = prefs.isComplete()

    suspend fun markOnboardingComplete() = prefs.markComplete()
}