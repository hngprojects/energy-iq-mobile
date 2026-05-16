package com.hng14.energyiq.features.onboarding

import com.hng14.energyiq.core.storage.FakePreferenceStore
import com.hng14.energyiq.features.onboarding.data.OnboardingPreferences
import com.hng14.energyiq.features.onboarding.data.OnboardingRepository
import com.hng14.energyiq.features.onboarding.data.remote.OnboardingApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingRepositoryTest {

    private val store = FakePreferenceStore()
    private val repo = OnboardingRepository(
        prefs = OnboardingPreferences(store),
        api = createOnboardingApi(),
    )

    @Test
    fun newUserHasNotCompletedOnboarding() = runTest {
        assertFalse(repo.hasCompletedOnboarding())
    }

    @Test
    fun markingCompleteReturnsTrueOnNextCheck() = runTest {
        repo.markOnboardingComplete()
        assertTrue(repo.hasCompletedOnboarding())
    }

    @Test
    fun completionStateDoesNotResetAcrossInstances() = runTest {
        repo.markOnboardingComplete()
        val repo2 = OnboardingRepository(
            prefs = OnboardingPreferences(store),
            api = createOnboardingApi(),
        )
        assertTrue(repo2.hasCompletedOnboarding())
    }

    private fun createOnboardingApi(): OnboardingApi {
        return OnboardingApi(
            httpClient = HttpClient(
                MockEngine { _ ->
                    respond(
                        content = """{"success":true,"message":"ok","data":[],"meta":{"timestamp":"2026-05-16T00:00:00.000Z"}}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                    )
                },
            ),
        )
    }
}
