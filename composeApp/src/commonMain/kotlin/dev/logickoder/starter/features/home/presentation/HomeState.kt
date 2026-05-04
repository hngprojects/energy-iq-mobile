package dev.logickoder.starter.features.home.presentation

import dev.logickoder.starter.features.auth.domain.model.User

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = true,
)