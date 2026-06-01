package com.hng14.energyiq.features.profile.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CloudinaryApi(
    private val httpClient: HttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun uploadImage(
        cloudName: String,
        uploadPreset: String,
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): CloudinaryUploadResponse {
        val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        try {
            println("Cloudinary: upload start cloud=$cloudName preset=$uploadPreset mime=$mimeType bytes=${bytes.size}")
            // Cloudinary unsigned uploads expect either multipart/form-data or x-www-form-urlencoded.
            // We default to multipart to avoid issues with very large base64 strings being url-encoded.
            //
            // NOTE: "file" can be raw bytes; Cloudinary will infer the format from Content-Type/filename.
            val safeFileName = fileName.substringAfterLast('/').substringAfterLast('\\')
            val content = MultiPartFormDataContent(
                formData {
                    append("upload_preset", uploadPreset)
                    append(
                        key = "file",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"file\"; filename=\"$safeFileName\"",
                            )
                        },
                    )
                },
            )

            val resp = httpClient.post(url) {
                setBody(content)
            }

            val raw = resp.bodyAsText()
            if (resp.status.value !in 200..299) {
                throw Exception("Cloudinary upload failed (${resp.status.value}): $raw")
            }
            return json.decodeFromString(CloudinaryUploadResponse.serializer(), raw)
        } catch (e: ClientRequestException) {
            val raw = runCatching { e.response.bodyAsText() }.getOrDefault("")
            throw Exception("Cloudinary upload failed (${e.response.status.value}): $raw")
        } catch (e: ResponseException) {
            val raw = runCatching { e.response.bodyAsText() }.getOrDefault("")
            throw Exception("Cloudinary upload failed (${e.response.status.value}): $raw")
        }
    }
}

@Serializable
data class CloudinaryUploadResponse(
    @SerialName("secure_url") val secureUrl: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("public_id") val publicId: String = "",
)
