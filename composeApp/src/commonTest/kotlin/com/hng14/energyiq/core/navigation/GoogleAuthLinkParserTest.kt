package com.hng14.energyiq.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GoogleAuthLinkParserTest {

    @Test
    fun extractsAccessTokenFromOnboardingFragment() {
        val token = GoogleAuthLinkParser.extractAccessToken(
            "https://staging.energy-iq.hng14.com/onboarding#token=abc123",
        )

        assertEquals("abc123", token)
    }

    @Test
    fun returnsNullForUnexpectedPath() {
        val token = GoogleAuthLinkParser.extractAccessToken(
            "https://staging.energy-iq.hng14.com/reset-password#token=abc123",
        )

        assertNull(token)
    }

    @Test
    fun returnsNullWhenFragmentTokenIsMissing() {
        val token = GoogleAuthLinkParser.extractAccessToken(
            "https://staging.energy-iq.hng14.com/onboarding",
        )

        assertNull(token)
    }
}
