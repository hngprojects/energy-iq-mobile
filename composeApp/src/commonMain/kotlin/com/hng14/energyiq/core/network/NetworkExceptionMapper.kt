package com.hng14.energyiq.core.network

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException

internal fun Throwable.toFriendlyNetworkException(): Exception {
    return when (this) {
        is HttpRequestTimeoutException -> Exception(
            "The request timed out. Please check your connection and try again.",
        )
        is ConnectTimeoutException -> Exception(
            "Unable to reach the server right now. Please check your connection and try again.",
        )
        is UnresolvedAddressException -> Exception(
            "No internet connection. Please check your network and try again.",
        )
        is IOException -> Exception(
            "A network error occurred. Please check your connection and try again.",
        )
        else -> Exception(
            message ?: "Something went wrong. Please try again.",
        )
    }
}
