package com.hng14.energyiq.features.chat.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.chat.data.remote.dto.CreateChatRequest
import com.hng14.energyiq.features.chat.data.remote.dto.CreateChatResponse
import com.hng14.energyiq.features.chat.data.remote.dto.GetChatMessagesResponse
import com.hng14.energyiq.features.chat.data.remote.dto.GetChatsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject

class ChatApi(
    private val httpClient: HttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun proxyOriginFromBaseUrl(): String {
        // BASE_URL is typically like: https://api.staging.energy-iq.hng14.com/api/v1
        // But the proxy endpoint is hosted on: https://staging.energy-iq.hng14.com/api/...
        val trimmed = NetworkConfig.BASE_URL.trimEnd('/')
        val origin = trimmed.substringBefore("/api/")
        return when {
            origin.startsWith("https://api.") -> "https://" + origin.removePrefix("https://api.")
            origin.startsWith("http://api.") -> "http://" + origin.removePrefix("http://api.")
            else -> origin
        }
    }

    suspend fun getChats(): GetChatsResponse {
        return try {
            val response = httpClient.get("${NetworkConfig.BASE_URL}/chats")
            val raw = response.bodyAsText()
            println("ChatApi: GET /chats status=${response.status.value} body=$raw")
            json.decodeFromString(GetChatsResponse.serializer(), raw)
        } catch (e: ClientRequestException) {
            throw e
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }

    suspend fun getChatMessages(
        chatId: String,
        userId: String,
    ): GetChatMessagesResponse {
        return try {
            // Backend deployments differ:
            // - staging:   {origin}/api/proxy/chats/{chatId}/messages?user_id=...
            // - local/api: {BASE_URL}/chats/{chatId}/messages?user_id=...
            //
            // Try staging-style proxy first, then fall back to direct v1 route.
            suspend fun fetch(url: String, label: String): Pair<Int, String> {
                val response = httpClient.get(url) { parameter("user_id", userId) }
                val raw = response.bodyAsText()
                println("ChatApi: GET $label status=${response.status.value} body=$raw")
                return response.status.value to raw
            }

            val origin = proxyOriginFromBaseUrl()
            val proxyUrl = "$origin/api/proxy/chats/$chatId/messages"
            val (proxyStatus, proxyRaw) = fetch(proxyUrl, "/api/proxy/chats/$chatId/messages")
            if (proxyStatus in 200..299) {
                return json.decodeFromString(GetChatMessagesResponse.serializer(), proxyRaw)
            }

            val directUrl = "${NetworkConfig.BASE_URL}/chats/$chatId/messages"
            val (_, directRaw) = fetch(directUrl, "/api/v1/chats/$chatId/messages")
            json.decodeFromString(GetChatMessagesResponse.serializer(), directRaw)
        } catch (e: ClientRequestException) {
            throw e
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }

    suspend fun createChat(startingMessage: String? = null): CreateChatResponse {
        return try {
            val response = httpClient.post("${NetworkConfig.BASE_URL}/chats") {
                contentType(ContentType.Application.Json)
                // Backend accepts create-chat without `startingMessage` (Postman flow), but rejects blank string.
                if (startingMessage == null) {
                    setBody(buildJsonObject { })
                } else {
                    setBody(CreateChatRequest(startingMessage = startingMessage))
                }
            }
            if (response.status.value in 200..299) {
                response.body()
            } else {
                throw io.ktor.client.plugins.ResponseException(response, "Server error: ${response.status.value}")
            }
        } catch (e: io.ktor.client.plugins.ResponseException) {
            throw e.toFriendlyNetworkException()
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }
}
