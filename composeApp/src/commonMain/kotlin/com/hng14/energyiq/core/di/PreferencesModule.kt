package com.hng14.energyiq.core.di

import com.hng14.energyiq.core.storage.createPreferenceStore
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.onboarding.data.OnboardingPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

fun preferencesModule(context: Any?): Module = module {
    single { createPreferenceStore(context) }
    single { OnboardingPreferences(get()) }
    single { AuthPreferences(get()) }
}
