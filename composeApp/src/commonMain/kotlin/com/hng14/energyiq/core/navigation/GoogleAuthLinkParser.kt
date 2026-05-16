package com.hng14.energyiq.core.navigation

import io.ktor.http.Url
import io.ktor.http.decodeURLQueryComponent

object GoogleAuthLinkParser {
    private const val Tag = "EnergyIQDeepLink"
    private const val OnboardingPath = "/onboarding"
    private val allowedHosts = setOf("staging.energy-iq.hng14.com")

    fun extractAccessToken(url: String?): String? {
        if (url.isNullOrBlank()) {
            println("$Tag google parser: url is blank")
            return null
        }

        return runCatching {
            val parsed = Url(url)
            if (parsed.protocol.name != "https") {
                println("$Tag google parser: rejected protocol=${parsed.protocol.name}")
                return null
            }
            if (parsed.host !in allowedHosts) {
                println("$Tag google parser: rejected host=${parsed.host}")
                return null
            }
            if (parsed.encodedPath != OnboardingPath) {
                println("$Tag google parser: rejected path=${parsed.encodedPath}")
                return null
            }

            val fragment = parsed.fragment
            val token = fragment
                .split("&")
                .firstOrNull { it.startsWith("token=") }
                ?.substringAfter("token=")
                ?.takeIf { it.isNotBlank() }
                ?.decodeURLQueryComponent()

            println("$Tag google parser: accepted host=${parsed.host} path=${parsed.encodedPath} tokenPresent=${token != null}")
            token
        }.getOrNull()
    }
}
