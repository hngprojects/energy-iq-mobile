package com.hng14.energyiq.features.profile.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class ProfileApi(
    private val httpClient: HttpClient,
) {
    suspend fun updatePersonalSettings(body: Map<String, String>) {
        try {
            val url = "${NetworkConfig.BASE_URL}/users/settings/personal"
            val response = httpClient.patch(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.value in 200..299) {
                // Some endpoints return no useful body; treat 2xx as success.
                runCatching { response.body<Unit>() }
                return
            }

            val raw = response.bodyAsText()
            val errorResponse = runCatching {
                Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(raw)
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
        } catch (e: ClientRequestException) {
            val raw = e.response.bodyAsText()
            val errorResponse = runCatching {
                Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(raw)
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${e.response.status.value})")
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }
}
