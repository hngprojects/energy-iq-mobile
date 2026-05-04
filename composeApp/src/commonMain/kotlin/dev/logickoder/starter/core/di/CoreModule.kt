package dev.logickoder.starter.core.di

import dev.logickoder.retrostash.core.RetrostashStore
import dev.logickoder.starter.core.database.AppDatabase
import dev.logickoder.starter.core.database.createDatabase
import dev.logickoder.starter.core.network.PreferenceRetrostashStore
import dev.logickoder.starter.core.network.createHttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

fun coreModule(context: Any?): Module = module {
    single { createDatabase(context = context) }
    single { get<AppDatabase>().userDao() }
    single<RetrostashStore> { PreferenceRetrostashStore(get()) }
    single { createHttpClient(get(), get()) }
    single<HttpClientEngine> { platformHttpEngine() }
}

expect fun platformHttpEngine(): HttpClientEngine
