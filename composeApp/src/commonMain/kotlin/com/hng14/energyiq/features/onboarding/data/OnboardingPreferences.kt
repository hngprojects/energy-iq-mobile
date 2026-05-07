package com.hng14.energyiq.features.onboarding.data

import com.hng14.energyiq.core.storage.PreferenceStore

class OnboardingPreferences(private val store: PreferenceStore) {
    suspend fun isComplete(): Boolean = store.get("onboarding_complete") == "true"
    suspend fun markComplete() {
        store.put("onboarding_complete", "true")
    }
}
