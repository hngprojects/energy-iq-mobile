package dev.logickoder.starter.core.di

import dev.logickoder.starter.features.auth.di.authModule
import dev.logickoder.starter.features.home.di.homeModule
import dev.logickoder.starter.features.onboarding.di.onboardingModule
import org.koin.dsl.KoinAppDeclaration

fun appDeclaration(context: Any? = null): KoinAppDeclaration = {
    modules(
        coreModule(context = context),
        preferencesModule(context = context),
        onboardingModule(),
        authModule(),
        homeModule(),
    )
}
