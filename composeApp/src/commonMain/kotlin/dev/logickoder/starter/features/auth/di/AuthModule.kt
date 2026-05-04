package dev.logickoder.starter.features.auth.di

import dev.logickoder.starter.features.auth.AuthMode
import dev.logickoder.starter.features.auth.data.AuthRepository
import dev.logickoder.starter.features.auth.data.remote.AuthApi
import dev.logickoder.starter.features.auth.presentation.AuthViewModel
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
