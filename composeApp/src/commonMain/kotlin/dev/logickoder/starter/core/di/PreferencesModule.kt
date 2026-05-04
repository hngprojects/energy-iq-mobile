package dev.logickoder.starter.core.di

import dev.logickoder.starter.core.storage.createPreferenceStore
import dev.logickoder.starter.features.auth.data.local.AuthPreferences
import dev.logickoder.starter.features.onboarding.data.OnboardingPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

fun preferencesModule(context: Any?): Module = module {
    single { createPreferenceStore(context) }
    single { OnboardingPreferences(get()) }
    single { AuthPreferences(get()) }
}
