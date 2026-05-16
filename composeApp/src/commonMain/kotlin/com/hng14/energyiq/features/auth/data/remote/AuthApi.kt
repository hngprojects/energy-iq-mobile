package com.hng14.energyiq.features.auth.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.core.network.toFriendlyNetworkException
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordResponse
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.LoginResponse
import com.hng14.energyiq.features.auth.data.remote.dto.MeResponse
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterResponse
import com.hng14.energyiq.features.auth.data.remote.dto.ResetPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.VerifyEmailRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AuthApi(
    private val httpClient: HttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/login",
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<LoginResponse>()
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

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/register",
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<RegisterResponse>()
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

    suspend fun forgotPassword(request: ForgotPasswordRequest): ForgotPasswordResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/forgot-password",
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<ForgotPasswordResponse>()
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

    suspend fun logout(token: String) {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/logout",
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.value in 200..299) {
                Unit
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

    suspend fun me(token: String): MeResponse {
        return try {
            val response = httpClient.get(
                "${NetworkConfig.BASE_URL}/auth/me",
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.value in 200..299) {
                val raw = response.bodyAsText()
                println("AuthApi.me raw response: $raw")
                json.decodeFromString<MeResponse>(raw)
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

    suspend fun resetPassword(request: ResetPasswordRequest) {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/reset-password",
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                Unit
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

    suspend fun verifyEmail(request: VerifyEmailRequest): LoginResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/verify-email",
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value in 200..299) {
                response.body<LoginResponse>()
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
}
