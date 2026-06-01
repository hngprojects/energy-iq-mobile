package com.hng14.energyiq.features.auth.di

import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.presentation.AuthViewModel
import com.hng14.energyiq.features.profile.data.ProfileRepository
import com.hng14.energyiq.features.profile.data.remote.CloudinaryApi
import com.hng14.energyiq.features.profile.data.remote.ProfileApi
import com.hng14.energyiq.features.profile.presentation.ProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun authModule(): Module = module {
    singleOf(::AuthApi)
    singleOf(::ProfileApi)
    singleOf(::CloudinaryApi)
    single { AuthRepository(get(), get(), get(), get()) }
    single { ProfileRepository(get(), get(), get()) }
    viewModel { params ->
        AuthViewModel(
            get(),
            initialMode = params.getOrNull<AuthMode>() ?: AuthMode.LOGIN,
            initialResetToken = params.getOrNull<String>(),
        )
    }
    viewModel { ProfileViewModel(get(), get()) }
}
