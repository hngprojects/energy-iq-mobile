package com.hng14.energyiq.core.di

import com.hng14.energyiq.features.alerts.di.alertsModule
import com.hng14.energyiq.features.auth.di.authModule
import com.hng14.energyiq.features.chat.di.chatModule
import com.hng14.energyiq.features.costAndSavings.di.costAndSavingsModule
import com.hng14.energyiq.features.home.di.homeModule
import com.hng14.energyiq.features.onboarding.di.onboardingModule
import com.hng14.energyiq.features.reports.di.reportsModule
import org.koin.dsl.KoinAppDeclaration

fun appDeclaration(context: Any? = null): KoinAppDeclaration = {
    modules(
        coreModule(context = context),
        preferencesModule(context = context),
        onboardingModule(),
        authModule(),
        homeModule(),
        alertsModule(),
        chatModule(),
        costAndSavingsModule(),
        reportsModule(),
        *platformModules().toTypedArray(),
    )
}

expect fun platformModules(): List<org.koin.core.module.Module>
