package com.hng14.energyiq.features.onboarding.di

import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
import com.hng14.energyiq.features.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun onboardingModule(): Module = module {
    singleOf(::OnboardingRepository)
    viewModel {
        OnboardingViewModel(get())
    }
}
