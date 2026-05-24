package com.hng14.energyiq.features.onboarding.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterRequest
import com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

class OnboardingApi(
    private val httpClient: HttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchSupportedBrands(): List<String> {
        return try {
            val response = httpClient.get("${NetworkConfig.BASE_URL}/inverters/supported-brands")
            if (response.status.value in 200..299) {
                parseSupportedBrands(response.bodyAsText())
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { json.decodeFromString<ApiErrorResponse>(body) }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { json.decodeFromString<ApiErrorResponse>(body) }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${e.response.status.value})")
        } catch (e: Exception) {
            print(e)
            throw e.toFriendlyNetworkException()
        }
    }

    suspend fun connectInverter(request: ConnectInverterRequest): ConnectInverterResponse {
        return try {
            val response = httpClient.post("${NetworkConfig.BASE_URL}/inverters/connect") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<ConnectInverterResponse>()
            } else {
                val body = response.bodyAsText()
                val errorResponse = runCatching { json.decodeFromString<ApiErrorResponse>(body) }.getOrNull()
                throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${response.status.value})")
            }
        } catch (e: ClientRequestException) {
            val body = e.response.bodyAsText()
            val errorResponse = runCatching { json.decodeFromString<ApiErrorResponse>(body) }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Request failed (${e.response.status.value})")
        } catch (e: Exception) {
            print(e)
            throw e.toFriendlyNetworkException()
        }
    }

    private fun parseSupportedBrands(raw: String): List<String> {
        val items = when (val root = json.parseToJsonElement(raw)) {
            is JsonArray -> root
            is JsonObject -> root["data"]?.jsonArray ?: JsonArray(emptyList())
            else -> JsonArray(emptyList())
        }

        return items.mapNotNull(::extractBrandName)
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
    }

    private fun extractBrandName(element: JsonElement): String? {
        return when (element) {
            is JsonPrimitive -> runCatching { element.content }.getOrNull()
            is JsonObject -> {
                element.string("name")
                    ?: element.string("brand")
                    ?: element.string("title")
            }
            else -> null
        }
    }

    private fun JsonObject.string(key: String): String? {
        return (this[key] as? JsonPrimitive)
            ?.let { primitive -> runCatching { primitive.content }.getOrNull() }
            ?.takeIf { it.isNotBlank() }
    }
}
