package dev.logickoder.starter.features.home.di

import dev.logickoder.starter.features.home.presentation.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun homeModule(): Module = module {
    viewModel {
        HomeViewModel(get())
    }
}
