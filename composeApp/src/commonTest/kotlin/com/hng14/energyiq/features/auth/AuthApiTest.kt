package com.hng14.energyiq.features.auth

import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthApiTest {

    private fun createApi(
        status: HttpStatusCode = HttpStatusCode.Created,
        responseBody: String =
            """
            {
              "success": true,
              "message": "Resource created successfully",
              "data": {
                "id": "user-1",
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User",
                "role": "user",
                "emailVerified": false,
                "createdAt": "2026-05-09T23:02:38.952Z",
                "updatedAt": "2026-05-09T23:02:38.952Z"
              },
              "meta": {
                "timestamp": "2026-05-09T23:02:39.020Z"
              }
            }
            """.trimIndent(),
    ): AuthApi {
        val engine = MockEngine {
            respond(
                content = responseBody,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    },
                )
            }
        }
        return AuthApi(client)
    }

    @Test
    fun loginReturnsMockedDemoPayload() = runTest {
        val api = createApi(
            status = HttpStatusCode.OK,
            responseBody = """
                {
                  "success": true,
                  "message": "Login successful",
                  "data": {
                    "accessToken": "demo_token_demo@example.com",
                    "refreshToken": "demo_refresh_token",
                    "user": {
                      "id": "user-1",
                      "email": "demo@example.com",
                      "firstName": "Demo",
                      "lastName": "User",
                      "role": "user",
                      "emailVerified": true,
                      "createdAt": "2026-05-09T23:02:38.952Z",
                      "updatedAt": "2026-05-09T23:02:38.952Z"
                    }
                  },
                  "meta": {
                    "timestamp": "2026-05-09T23:02:39.020Z"
                  }
                }
            """.trimIndent()
        )
        val response = api.login(LoginRequest(email = "demo@example.com", password = "password123"))
        assertEquals("demo@example.com", response.data.user.email)
        assertEquals("demo_token_demo@example.com", response.data.accessToken)
    }

    @Test
    fun registerParsesWrappedSuccessResponse() = runTest {
        val response = createApi().register(
            RegisterRequest(
                firstName = "Test",
                lastName = "User",
                email = "test@example.com",
                password = "password123",
            ),
        )
        assertEquals("test@example.com", response.data.email)
        assertEquals("Test", response.data.firstName)
        assertEquals("User", response.data.lastName)
    }

    @Test
    fun registerThrowsBackendMessageForConflictResponse() = runTest {
        val api = createApi(
            status = HttpStatusCode.Conflict,
            responseBody =
                """
                {
                  "success": false,
                  "message": "The request conflicts with the current resource state",
                  "error": "Conflict",
                  "statusCode": 409,
                  "meta": {
                    "timestamp": "2026-05-10T01:14:19.267Z"
                  }
                }
                """.trimIndent(),
        )

        val error = assertFailsWith<Exception> {
            api.register(
                RegisterRequest(
                    firstName = "Test",
                    lastName = "User",
                    email = "test@example.com",
                    password = "password123",
                ),
            )
        }

        assertEquals("The request conflicts with the current resource state", error.message)
    }
}
