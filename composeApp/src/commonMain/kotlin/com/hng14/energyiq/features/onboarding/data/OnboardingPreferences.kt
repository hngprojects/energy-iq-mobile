package com.hng14.energyiq.features.onboarding.data

import com.hng14.energyiq.core.storage.PreferenceStore
import com.hng14.energyiq.features.auth.data.local.AuthPreferences

class OnboardingPreferences(
    private val store: PreferenceStore,
    private val authPreferences: AuthPreferences
) {

    suspend fun isComplete(): Boolean =
        store.get(authPreferences.getUserScopedKey("onboarding_complete")) == "true"

    suspend fun markComplete() {
        store.put(authPreferences.getUserScopedKey("onboarding_complete"), "true")
    }

    suspend fun saveInverterId(id: String) {
        store.put(authPreferences.getUserScopedKey("inverter_id"), id)
    }

    suspend fun getInverterId(): String? =
        store.get(authPreferences.getUserScopedKey("inverter_id"))

}
