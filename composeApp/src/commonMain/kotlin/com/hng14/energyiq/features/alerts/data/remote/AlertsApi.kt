package com.hng14.energyiq.features.alerts.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.features.alerts.data.remote.dto.AlertsResponse
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class AlertsApi(
    private val httpClient: HttpClient,
) {
    suspend fun fetchAlerts(
        alertType: String?,
        pageNumber: Int,
        pageSize: Int,
    ): AlertsResponse {
        return try {
            val url = if (NetworkConfig.BASE_URL.endsWith("/")) {
                "${NetworkConfig.BASE_URL}alerts"
            } else {
                "${NetworkConfig.BASE_URL}/alerts"
            }
            val response = httpClient.get(url) {
                if (!alertType.isNullOrBlank()) {
                    parameter("alert_type", alertType)
                }
                parameter("page_number", pageNumber)
                parameter("page_size", pageSize)
            }
            if (response.status.value in 200..299) {
                response.body<AlertsResponse>()
            } else {
                val errorResponse = response.body<ApiErrorResponse>()
                throw Exception(errorResponse.message.toErrorMessage())
            }
        } catch (e: ClientRequestException) {
            val errorResponse = e.response.body<ApiErrorResponse>()
            throw Exception(errorResponse.message.toErrorMessage())
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }
}

