package com.hng14.energyiq.features.costAndSavings.di

import com.hng14.energyiq.features.costAndSavings.data.CostAndSavingsRepository
import com.hng14.energyiq.features.costAndSavings.presentation.CostAndSavingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun costAndSavingsModule() = module {
    single { CostAndSavingsRepository(get(), get()) }
    viewModel { CostAndSavingsViewModel(get(), get(), get()) }
}
