package com.hng14.energyiq.features.home.di

import com.hng14.energyiq.features.home.data.HomeRepository
import com.hng14.energyiq.features.home.data.HealthLogRepository
import com.hng14.energyiq.features.home.data.remote.InverterApi
import com.hng14.energyiq.features.home.presentation.HomeViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun homeModule(): Module = module {
    single { InverterApi(get(), get()) }
    single { HealthLogRepository(get()) }
    single {
        HomeRepository(
            inverterApi = get(),
            onboardingRepository = get(),
            store = get(),
            authPreferences = get(),
            json = Json { ignoreUnknownKeys = true }
        )
    }
    viewModel {
        HomeViewModel(get(), get(), get())
    }
}
