package com.hng14.energyiq.features.reports.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.reports.data.remote.dto.ReportRequest
import com.hng14.energyiq.features.reports.data.remote.dto.ReportResponse
import com.hng14.energyiq.features.reports.data.remote.dto.ReportsListResponse
import com.hng14.energyiq.features.reports.data.remote.dto.ReportDeleteResponse
import com.hng14.energyiq.features.reports.data.remote.dto.EmailReportResponse
import com.hng14.energyiq.features.reports.data.remote.dto.ReportCancelResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class ReportsApi(
    private val httpClient: HttpClient,
    private val authPreferences: AuthPreferences,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createReport(request: ReportRequest): ReportResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}reports"
            } else {
                "${NetworkConfig.BASE_URL}/reports"
            }
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<ReportResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    json.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                json.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: e.message)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unknown network error occurred")
        }
    }

    suspend fun getReports(): ReportsListResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}reports"
            } else {
                "${NetworkConfig.BASE_URL}/reports"
            }
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<ReportsListResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    json.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                json.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: e.message)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unknown network error occurred")
        }
    }

    suspend fun deleteReport(id: String): ReportDeleteResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}reports/$id"
            } else {
                "${NetworkConfig.BASE_URL}/reports/$id"
            }
            val response = httpClient.delete(url) {
                contentType(ContentType.Application.Json)
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<ReportDeleteResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    json.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                json.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: e.message)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unknown network error occurred")
        }
    }

    suspend fun emailReport(id: String): EmailReportResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}reports/email-report/$id"
            } else {
                "${NetworkConfig.BASE_URL}/reports/email-report/$id"
            }
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<EmailReportResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    json.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                json.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: e.message)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unknown network error occurred")
        }
    }

    suspend fun cancelReport(id: String): ReportCancelResponse {
        val token = authPreferences.getToken()
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}reports/cancel/$id"
            } else {
                "${NetworkConfig.BASE_URL}/reports/cancel/$id"
            }
            val response = httpClient.patch(url) {
                contentType(ContentType.Application.Json)
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            if (response.status.value in 200..299) {
                response.body<ReportCancelResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { 
                    json.decodeFromString<ApiErrorResponse>(body) 
                }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { 
                json.decodeFromString<ApiErrorResponse>(body) 
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: e.message)
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unknown network error occurred")
        }
    }
}
