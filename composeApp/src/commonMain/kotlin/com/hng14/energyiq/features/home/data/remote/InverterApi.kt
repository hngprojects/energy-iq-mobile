package com.hng14.energyiq.features.home.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

class InverterApi(
    private val httpClient: HttpClient,
    private val authPreferences: AuthPreferences,
) {
    suspend fun fetchInverterDashboard(
        inverterId: String
    ): InverterMetricsResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}inverter-metrics/$inverterId/dashboard"
            } else {
                "${NetworkConfig.BASE_URL}/inverter-metrics/$inverterId/dashboard"
            }
            val response = httpClient.get(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<InverterMetricsResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${e.response.status.value})")
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }
}
