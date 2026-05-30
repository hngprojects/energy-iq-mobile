package com.hng14.energyiq.features.chat.di

import com.hng14.energyiq.core.socket.AgentSocket
import com.hng14.energyiq.core.socket.SocketIoAgentSocket
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-only overrides for chat dependencies.
 */
fun chatAndroidModule(): Module = module {
    single<AgentSocket> { SocketIoAgentSocket(authPreferences = get()) }
}

