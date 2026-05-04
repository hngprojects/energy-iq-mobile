package dev.logickoder.starter.core.navigation

import dev.logickoder.starter.features.auth.AuthMode
import kotlinx.serialization.Serializable

@Serializable
sealed class AppDestination {

    @Serializable
    data object Onboarding : AppDestination()

    @Serializable
    data class Auth(val initialMode: AuthMode = AuthMode.LOGIN) : AppDestination()

    @Serializable
    data object Home : AppDestination()
}
