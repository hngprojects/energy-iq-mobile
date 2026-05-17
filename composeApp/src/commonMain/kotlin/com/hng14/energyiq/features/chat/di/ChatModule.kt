package com.hng14.energyiq.features.chat.di

import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.presentation.ChatListViewModel
import com.hng14.energyiq.features.chat.presentation.ChatViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun chatModule(): Module = module {
    singleOf(::ChatRepository)
    viewModelOf(::ChatListViewModel)
    viewModel { params ->
        ChatViewModel(
            repository = get(),
            conversationId = params.getOrNull<String>()
        )
    }
}
