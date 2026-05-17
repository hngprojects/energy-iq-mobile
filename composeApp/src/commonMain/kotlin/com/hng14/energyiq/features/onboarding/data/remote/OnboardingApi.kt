package com.hng14.energyiq.features.onboarding.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

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
                val errorResponse = response.body<ApiErrorResponse>()
                throw Exception(errorResponse.message)
            }
        } catch (e: ClientRequestException) {
            val errorResponse = e.response.body<ApiErrorResponse>()
            print(errorResponse)
            throw Exception(errorResponse.message)
        } catch (e: Exception) {
            print(e)
            throw e.toFriendlyNetworkException()
        }
    }

    private fun parseSupportedBrands(raw: String): List<String> {
        val root = json.parseToJsonElement(raw)
        val items = when (root) {
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
