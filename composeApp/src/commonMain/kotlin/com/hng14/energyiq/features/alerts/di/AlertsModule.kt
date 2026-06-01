package com.hng14.energyiq.features.alerts.di

import com.hng14.energyiq.features.alerts.data.AlertRepository
import com.hng14.energyiq.features.alerts.data.remote.AlertsApi
import com.hng14.energyiq.features.alerts.presentation.AlertViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun alertsModule(): Module = module {
    single { AlertsApi(get(), get()) }
    single {
        AlertRepository(
            api = get(),
            store = get(),
            authPrefs = get(),
            json = Json { ignoreUnknownKeys = true }
        )
    }
    viewModelOf(::AlertViewModel)
}
