package com.hng14.energyiq.core.di

import com.hng14.energyiq.core.database.AppDatabase
import com.hng14.energyiq.core.database.createDatabase
import com.hng14.energyiq.core.network.createHttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

fun coreModule(context: Any?): Module = module {
    single { createDatabase(context = context) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().batteryHealthLogDao() }
    single { createHttpClient(get(), get()) }
    single<HttpClientEngine> { platformHttpEngine() }
}

expect fun platformHttpEngine(): HttpClientEngine
