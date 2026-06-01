package com.hng14.energyiq.features.alerts.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.features.alerts.data.remote.dto.AlertSummaryResponse
import com.hng14.energyiq.features.alerts.data.remote.dto.AlertsResponse
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.datetime.Clock

class AlertsApi(
    private val httpClient: HttpClient,
    private val authPreferences: AuthPreferences,
) {
    suspend fun fetchAlerts(
        alertType: String?
    ): AlertsResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}alerts"
            } else {
                "${NetworkConfig.BASE_URL}/alerts"
            }
            val response = httpClient.get(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                
                if (!alertType.isNullOrBlank()) {
                    parameter("alert_type", alertType)
                }
            }
            if (response.status.value in 200..299) {
                response.body<AlertsResponse>()
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

    suspend fun fetchAlertSummary(): AlertSummaryResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}alerts/summary"
            } else {
                "${NetworkConfig.BASE_URL}/alerts/summary"
            }
            val response = httpClient.get(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<AlertSummaryResponse>()
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

    suspend fun resolveAlert(alertId: String) {
        val token = authPreferences.getToken()
        try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}alerts/$alertId/resolve"
            } else {
                "${NetworkConfig.BASE_URL}/alerts/$alertId/resolve"
            }
            val response = httpClient.patch(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value !in 200..299) {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Resolution failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Resolution failed (${e.response.status.value})")
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }
}

