package com.hng14.energyiq.features.auth.di

import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.presentation.AuthViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun authModule(): Module = module {
    singleOf(::AuthApi)
    singleOf(::AuthRepository)
    viewModel { params ->
        AuthViewModel(
            get(),
            initialMode = params.getOrNull() ?: AuthMode.LOGIN,
        )
    }
}
