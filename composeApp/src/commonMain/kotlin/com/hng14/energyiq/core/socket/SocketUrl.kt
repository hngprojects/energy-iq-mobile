package com.hng14.energyiq.core.socket

/**
 * Derive a socket base URL from the REST API base URL.
 *
 * Example:
 * - https://api.staging.energy-iq.hng14.com/api/v1 -> https://api.staging.energy-iq.hng14.com
 */
object SocketUrl {
    fun fromApiBaseUrl(apiBaseUrl: String): String {
        var url = apiBaseUrl.trim().removeSuffix("/")
        if (url.endsWith("/api/v1")) {
            url = url.removeSuffix("/api/v1")
        }
        return url.removeSuffix("/")
    }
}

