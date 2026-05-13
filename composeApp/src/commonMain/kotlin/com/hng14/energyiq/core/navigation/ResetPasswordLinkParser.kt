package com.hng14.energyiq.core.navigation

import io.ktor.http.Url

object ResetPasswordLinkParser {
    private const val Tag = "EnergyIQDeepLink"
    private const val ResetPasswordPath = "/reset-password"
    private val allowedHosts = setOf("staging.energy-iq.hng14.com")

    fun extractToken(url: String?): String? {
        if (url.isNullOrBlank()) {
            println("$Tag parser: url is blank")
            return null
        }

        return runCatching {
            val parsed = Url(url)
            if (parsed.protocol.name != "https") {
                println("$Tag parser: rejected protocol=${parsed.protocol.name}")
                return null
            }
            if (parsed.host !in allowedHosts) {
                println("$Tag parser: rejected host=${parsed.host}")
                return null
            }
            if (parsed.encodedPath != ResetPasswordPath) {
                println("$Tag parser: rejected path=${parsed.encodedPath}")
                return null
            }

            val token = parsed.parameters["token"]?.takeIf { it.isNotBlank() }
            println("$Tag parser: accepted host=${parsed.host} path=${parsed.encodedPath} tokenPresent=${token != null}")
            token
        }.getOrNull()
    }
}
