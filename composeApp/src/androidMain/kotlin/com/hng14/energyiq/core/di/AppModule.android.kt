package com.hng14.energyiq.core.di

import com.hng14.energyiq.features.chat.di.chatAndroidModule
import org.koin.core.module.Module

actual fun platformModules(): List<Module> = listOf(
    chatAndroidModule(),
)

