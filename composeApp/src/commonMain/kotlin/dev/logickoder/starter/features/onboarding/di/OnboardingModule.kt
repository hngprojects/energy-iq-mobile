package dev.logickoder.starter.features.onboarding.di

import dev.logickoder.starter.features.onboarding.data.OnboardingRepository
import dev.logickoder.starter.features.onboarding.presentation.OnboardingViewModel
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
