package dev.logickoder.starter.core.network

import dev.logickoder.retrostash.core.RetrostashStore
import dev.logickoder.retrostash.ktor.RetrostashPlugin
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(engine: HttpClientEngine, retrostashStore: RetrostashStore): HttpClient =
    HttpClient(
        engine = engine,
        block = {
            installRetrostash(store = retrostashStore)
            installContentNegotiation()
            installLogging()
            installTimeout()
        },
    )

private fun HttpClientConfig<*>.installRetrostash(store: RetrostashStore) {
    install(plugin = RetrostashPlugin) {
        this.store = store
    }
}

private fun HttpClientConfig<*>.installContentNegotiation() {
    install(plugin = ContentNegotiation) {
        json(json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }
}

private fun HttpClientConfig<*>.installLogging() {
    install(plugin = Logging) {
        logger = Logger.SIMPLE
        level = if (NetworkConfig.IS_DEBUG) LogLevel.ALL else LogLevel.NONE
    }
}

private fun HttpClientConfig<*>.installTimeout() {
    install(plugin = HttpTimeout) {
        requestTimeoutMillis = NetworkConfig.TIMEOUT_MS
        connectTimeoutMillis = NetworkConfig.TIMEOUT_MS
        socketTimeoutMillis = NetworkConfig.TIMEOUT_MS
    }
}
