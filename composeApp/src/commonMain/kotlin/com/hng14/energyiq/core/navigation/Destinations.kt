package com.hng14.energyiq.core.navigation

import com.hng14.energyiq.features.auth.AuthMode
import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {

    @Serializable
    data object Onboarding : AppDestination()

    @Serializable
    data class Auth(
        val initialMode: AuthMode = AuthMode.LOGIN,
        val initialResetToken: String? = null,
    ) : AppDestination()

    @Serializable
    data object InverterSetup : AppDestination()

    @Serializable
    data object Home : AppDestination()
    @Serializable
    data object EmailVerification : AppDestination()
}
