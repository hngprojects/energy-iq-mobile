package com.hng14.energyiq.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResetPasswordLinkParserTest {

    @Test
    fun extractsTokenFromStagingResetPasswordLink() {
        val token = ResetPasswordLinkParser.extractToken(
            "https://staging.energy-iq.hng14.com/reset-password?token=abc123",
        )

        assertEquals("abc123", token)
    }

    @Test
    fun returnsNullForUnexpectedHost() {
        val token = ResetPasswordLinkParser.extractToken(
            "https://energy-iq.hng14.com/reset-password?token=abc123",
        )

        assertNull(token)
    }

    @Test
    fun returnsNullWhenTokenIsMissing() {
        val token = ResetPasswordLinkParser.extractToken(
            "https://staging.energy-iq.hng14.com/reset-password",
        )

        assertNull(token)
    }
}
