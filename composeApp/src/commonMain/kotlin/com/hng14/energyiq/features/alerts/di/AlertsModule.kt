package com.hng14.energyiq.features.alerts.di

import com.hng14.energyiq.features.alerts.data.AlertRepository
import com.hng14.energyiq.features.alerts.presentation.AlertViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun alertsModule(): Module = module {
    singleOf(::AlertRepository)
    viewModelOf(::AlertViewModel)
}
