package com.hng14.energyiq.features.profile.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toErrorMessage
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.profile.data.remote.dto.ProfileImageUploadResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.cio.MultipartEvent
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class ProfileApi(
    private val httpClient: HttpClient,
) {
    suspend fun updatePersonalSettings(body: Map<String, JsonElement>) {
        try {
            val url = "${NetworkConfig.BASE_URL}/users/settings/personal"
            val response = httpClient.patch(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.value in 200..299) {
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

    suspend fun uploadProfileImage(
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ): String{

        try {
            val url = "${NetworkConfig.BASE_URL}/users/settings/personal/img"
            val response = httpClient.post(url){
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "file",
                                value = bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"$fileName\""
                                    )
                                }
                            )
                        }
                    )
                )
            }

            val raw = response.bodyAsText()
            if (response.status.value !in 200..299) {
                val errorResponse = runCatching {
                    Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(
                        raw
                    )
                }.getOrNull()
                throw Exception(
                    errorResponse?.message?.toErrorMessage()
                        ?: "Upload failed (${response.status.value})"
                )
            }
            val parsed = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<ProfileImageUploadResponse>(raw)

            return parsed.data.uploadUrl

        }catch (e: ClientRequestException){
            val raw = e.response.bodyAsText()
            val errorResponse = runCatching {
                Json { ignoreUnknownKeys = true }.decodeFromString<ApiErrorResponse>(raw)
            }.getOrNull()
            throw Exception(errorResponse?.message?.toErrorMessage() ?: "Upload failed (${e.response.status.value})")
        } catch (e: Exception) {
            throw e.toFriendlyNetworkException()
        }
    }

    private fun extractProfileUrl(element: kotlinx.serialization.json.JsonElement): String? {
        val obj = element as? kotlinx.serialization.json.JsonObject ?: return null
        val directUrl =
            obj["profileUrl"]?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
                ?: obj["profile_url"]?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
        if (!directUrl.isNullOrBlank()) return directUrl
        val nestedObj = (obj["data"] ?: obj["user"]) as? kotlinx.serialization.json.JsonObject
        if (nestedObj != null) {
            return nestedObj["profileUrl"]?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
                ?: nestedObj["profile_url"]?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
        }
        return null
    }

}
