package com.hng14.energyiq.features.home.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.home.data.remote.dto.InverterMetricsResponse
import com.hng14.energyiq.features.home.data.remote.dto.InverterSavingsResponse
import com.hng14.energyiq.features.home.data.remote.dto.UserInvertersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

class InverterApi(
    private val httpClient: HttpClient,
    private val authPreferences: AuthPreferences,
) {
    private fun proxyOriginFromBaseUrl(): String {
        // BASE_URL is typically like: https://api.staging.energy-iq.hng14.com/api/v1
        // But the proxy endpoint is hosted on: https://staging.energy-iq.hng14.com/api/...
        val origin = NetworkConfig.BASE_URL.substringBefore("/api")
        return when {
            origin.startsWith("https://api.") -> "https://" + origin.removePrefix("https://api.")
            origin.startsWith("http://api.") -> "http://" + origin.removePrefix("http://api.")
            else -> origin
        }
    }

    suspend fun fetchInverterSavings(
        inverterId: String,
        period: String,
        date: String,
        startDate: String? = null,
        endDate: String? = null
    ): InverterSavingsResponse {
        val token = authPreferences.getToken()
        return try {
            val url = "${NetworkConfig.BASE_URL}/inverter-metrics/$inverterId/savings"
            val response = httpClient.get(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                parameter("period", period)
                parameter("date", date)
                if (startDate != null) parameter("startDate", startDate)
                if (endDate != null) parameter("endDate", endDate)
            }
            if (response.status.value in 200..299) {
                response.body<InverterSavingsResponse>()
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

    suspend fun fetchInverterDashboard(
        inverterId: String
    ): InverterMetricsResponse {
        val token = authPreferences.getToken()
        return try {
            val url = "${NetworkConfig.BASE_URL}/inverter-metrics/$inverterId/dashboard"
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

    suspend fun fetchUserInverters(userId: String): UserInvertersResponse {
        val token = authPreferences.getToken()
        return try {
            // Primary (Swagger) endpoint:
            // https://api.<env>.energy-iq.hng14.com/api/v1/inverters/user/{userId}
            val base = NetworkConfig.BASE_URL.trimEnd('/')
            val url = "$base/inverters/user/$userId"

            val response = httpClient.get(url) {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<UserInvertersResponse>()
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
