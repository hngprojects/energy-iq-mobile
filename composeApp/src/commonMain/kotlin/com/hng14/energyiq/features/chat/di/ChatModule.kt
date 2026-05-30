package com.hng14.energyiq.features.chat.di

import com.hng14.energyiq.core.socket.AgentSocket
import com.hng14.energyiq.core.socket.UnsupportedAgentSocket
import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.data.remote.ChatApi
import com.hng14.energyiq.features.chat.presentation.ChatListViewModel
import com.hng14.energyiq.features.chat.presentation.ChatViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun chatModule(): Module = module {
    singleOf(::ChatApi)
    singleOf(::ChatRepository)
    // Android overrides this binding with the real Socket.IO implementation.
    single<AgentSocket> { UnsupportedAgentSocket() }
    viewModel {
        ChatListViewModel(
            repository = get(),
            agentSocket = get(),
            authPreferences = get(),
        )
    }
    viewModel { params ->
        ChatViewModel(
            repository = get(),
            agentSocket = get(),
            authPreferences = get(),
            conversationId = params.getOrNull<String>()
        )
    }
}
