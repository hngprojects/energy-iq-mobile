package com.hng14.energyiq.core.network

import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

fun createHttpClient(
    engine: HttpClientEngine,
    authPreferences: AuthPreferences,
): HttpClient {
    val firstPartyHosts = buildFirstPartyHosts()
    val client = HttpClient(
        engine = engine,
        block = {
            installContentNegotiation()
            installTimeout()

            // Custom Interceptor to ensure we always use the latest token from storage
            // This fixes the bug where Ktor's Auth plugin caches tokens and sends the wrong user's token after switching accounts
            install("FreshTokenInterceptor") {
                requestPipeline.intercept(HttpRequestPipeline.State) {
                    val urlString = context.url.buildString()
                    val isAuthPath = urlString.contains("/auth/login") ||
                            urlString.contains("/auth/register") ||
                            urlString.contains("/auth/forgot-password") ||
                            urlString.contains("/auth/reset-password") ||
                            urlString.contains("/auth/verify-email") ||
                            urlString.contains("/auth/google")

                    val isFirstParty = context.url.host in firstPartyHosts
                    if (!isAuthPath && isFirstParty && context.headers[HttpHeaders.Authorization] == null) {
                        val token = authPreferences.getToken()?.trim()
                        if (!token.isNullOrBlank()) {
                            context.header(HttpHeaders.Authorization, "Bearer $token")
                        }
                    }
                }
            }

            installLogging()
        },
    )

    // Ktor 3: HttpSend doesn't need installation; attach interceptor via plugin() API.
    client.installRefreshOnUnauthorized(authPreferences = authPreferences)

    return client
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

private fun HttpClientConfig<*>.installTimeout() {
    install(HttpTimeout) {
        requestTimeoutMillis = 120_000 // 2 minutes to allow for AI processing
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 120_000
    }
}

private fun HttpClient.installRefreshOnUnauthorized(authPreferences: AuthPreferences) {
    val refreshMutex = Mutex()
    val firstPartyHosts = buildFirstPartyHosts()

    plugin(HttpSend).intercept { request ->
        val response = execute(request)

        // Only attempt refresh on 401 responses for non-auth endpoints.
        val urlString = request.url.buildString()
        val isFirstParty = request.url.host in firstPartyHosts
        val isAuthPath = urlString.contains("/auth/login") ||
            urlString.contains("/auth/register") ||
            urlString.contains("/auth/forgot-password") ||
            urlString.contains("/auth/reset-password") ||
            urlString.contains("/auth/verify-email") ||
            urlString.contains("/auth/google") ||
            urlString.contains("/auth/refresh")

        if (response.response.status.value != 401 || isAuthPath || !isFirstParty) return@intercept response

        val refreshToken = authPreferences.getRefreshToken()?.trim().orEmpty()
        if (refreshToken.isBlank()) {
            authPreferences.clearSession()
            return@intercept response
        }

        // Ensure only one refresh runs at a time.
        val newAccessToken = refreshMutex.withLock {
            // Another request may have refreshed already; re-check token.
            val currentToken = authPreferences.getToken()?.trim().orEmpty()
            val requestToken = request.headers[HttpHeaders.Authorization]
                ?.removePrefix("Bearer ")
                ?.trim()
                .orEmpty()
            if (currentToken.isNotBlank() && currentToken != requestToken) {
                return@withLock currentToken
            }

            val raw = post("${NetworkConfig.BASE_URL}/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }.bodyAsText()

            val parsed = Json.parseToJsonElement(raw).jsonObject
            val data = parsed["data"]?.jsonObject ?: JsonObject(emptyMap())
            val token = data.string("accessToken")
            val rotatedRefreshToken = data.string("refreshToken").ifBlank { refreshToken }

            if (token.isBlank()) {
                authPreferences.clearSession()
                ""
            } else {
                authPreferences.saveTokens(token = token, refreshToken = rotatedRefreshToken)
                token
            }
        }

        if (newAccessToken.isBlank()) return@intercept response

        // Retry the original request once with the refreshed token.
        val retry = HttpRequestBuilder().takeFrom(request)
        retry.headers.remove(HttpHeaders.Authorization)
        retry.header(HttpHeaders.Authorization, "Bearer $newAccessToken")
        execute(retry)
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

private fun buildFirstPartyHosts(): Set<String> {
    // We should only attach our Bearer token / run refresh logic for our own backend hosts.
    // Third-party services (e.g., Cloudinary) will reject/interpret Authorization headers.
    val base = NetworkConfig.BASE_URL.trim()
    val apiHost = runCatching { Url(base).host }.getOrNull()
    val originHost = runCatching { Url(base.substringBefore("/api/")).host }.getOrNull()
    return buildSet {
        if (!apiHost.isNullOrBlank()) add(apiHost)
        if (!originHost.isNullOrBlank()) add(originHost)
    }
}
