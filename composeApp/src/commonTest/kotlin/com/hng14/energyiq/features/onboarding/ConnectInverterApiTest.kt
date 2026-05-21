package com.hng14.energyiq.features.onboarding

import com.hng14.energyiq.features.onboarding.data.remote.OnboardingApi
import com.hng14.energyiq.features.onboarding.data.remote.dto.ConnectInverterRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConnectInverterApiTest {

    @Test
    fun connectVictronHitsExpectedEndpointAndSendsToken() = runTest {
        val client = HttpClient(
            MockEngine { request ->
                assertTrue(request.url.encodedPath.endsWith("/inverters/connect"))
                assertEquals("POST", request.method.value)

                val body = request.body
                val bodyText = (body as? TextContent)?.text.orEmpty()
                assertTrue(bodyText.contains("\"brand\":\"VICTRON\""))
                assertTrue(bodyText.contains("\"victronAccessToken\":\"victron-token\""))

                respond(
                    content = """{"success":true,"message":"connected","data":null,"meta":{"timestamp":"2026-05-20T00:00:00.000Z"}}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                )
            },
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
            }
        }

        val api = OnboardingApi(httpClient = client)
        val response = api.connectInverter(
            request = ConnectInverterRequest(
                brand = "VICTRON",
                victronAccessToken = "victron-token",
            ),
        )
        assertTrue(response.success)
    }
}

