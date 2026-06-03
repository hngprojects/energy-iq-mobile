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
    data object HomeProfile : AppDestination()

    @Serializable
    data object SmartAlerts : AppDestination()

    @Serializable
    data object ChatEntry : AppDestination()

    @Serializable
    data object Chat : AppDestination()

    @Serializable
    data object ChatList : AppDestination()

    @Serializable
    data class ChatDetail(
        val conversationId: String,
    ) : AppDestination()

    @Serializable
    data class EmailVerification(
        val fullName: String,
        val email: String,
    ) : AppDestination()

    @Serializable
    data object CostAndSavings : AppDestination()

    @Serializable
    data class WebPage(
        val title: String,
        val url: String,
    ) : AppDestination()
}
