package com.hng14.energyiq.features.reports.di

import com.hng14.energyiq.features.reports.data.ReportsRepository
import com.hng14.energyiq.features.reports.data.remote.ReportsApi
import com.hng14.energyiq.features.reports.presentation.ReportsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun reportsModule(): Module = module {
    single { ReportsApi(get(), get()) }
    single { ReportsRepository(get()) }
    viewModelOf(::ReportsViewModel)
}
