package com.hng14.energyiq.core.network

import dev.logickoder.retrostash.core.RetrostashStore
import dev.logickoder.retrostash.ktor.RetrostashPlugin
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun createHttpClient(
    engine: HttpClientEngine,
    retrostashStore: RetrostashStore,
    authPreferences: AuthPreferences,
): HttpClient =
    HttpClient(
        engine = engine,
        block = {
            installRetrostash(store = retrostashStore)
            installContentNegotiation()
            installAuth(authPreferences = authPreferences)
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

private fun HttpClientConfig<*>.installAuth(authPreferences: AuthPreferences) {
    install(Auth) {
        bearer {
            loadTokens {
                val accessToken = authPreferences.getToken()?.trim().orEmpty()
                val refreshToken = authPreferences.getRefreshToken()?.trim().orEmpty()
                if (accessToken.isBlank()) {
                    null
                } else {
                    BearerTokens(accessToken = accessToken, refreshToken = refreshToken)
                }
            }

            refreshTokens {
                val refreshToken = oldTokens?.refreshToken?.trim().orEmpty()
                if (refreshToken.isBlank()) {
                    authPreferences.clearSession()
                    return@refreshTokens null
                }

                val raw = client.post("${NetworkConfig.BASE_URL}/auth/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("refreshToken" to refreshToken))
                }.bodyAsText()

                val parsed = Json.parseToJsonElement(raw).jsonObject
                val data = parsed["data"]?.jsonObject ?: JsonObject(emptyMap())
                val newAccessToken = data.string("accessToken")
                val rotatedRefreshToken = data.string("refreshToken").ifBlank { refreshToken }

                if (newAccessToken.isBlank()) {
                    authPreferences.clearSession()
                    null
                } else {
                    authPreferences.saveTokens(
                        token = newAccessToken,
                        refreshToken = rotatedRefreshToken,
                    )
                    BearerTokens(accessToken = newAccessToken, refreshToken = rotatedRefreshToken)
                }
            }

            sendWithoutRequest { request ->
                request.url.host == NetworkConfig.BASE_URL.removePrefix("https://").removePrefix("http://").substringBefore("/")
            }
        }
    }
}

private fun JsonObject.string(key: String): String {
    val value = this[key] as? JsonPrimitive ?: return ""
    return runCatching { value.content }.getOrDefault("")
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
