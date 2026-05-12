package com.hng14.energyiq.features.auth.data.remote

import com.hng14.energyiq.core.network.NetworkConfig
import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.LoginResponse
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterResponse
import com.hng14.energyiq.features.auth.data.remote.dto.VerifyEmailRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(
    private val httpClient: HttpClient
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/login"
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
            throw e
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/register"
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
            throw e
        }
    }

    suspend fun logout(token: String) {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/logout"
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
            throw e
        }
    }

    suspend fun verifyEmail(request: VerifyEmailRequest) {
        return try {
            val response = httpClient.post(
                "${NetworkConfig.BASE_URL}/auth/verify-email"
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
            throw Exception(errorResponse.message)
        } catch (e: Exception) {
            throw e
        }
    }
}
