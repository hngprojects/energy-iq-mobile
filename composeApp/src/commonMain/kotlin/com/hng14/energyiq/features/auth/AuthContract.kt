package com.hng14.energyiq.features.auth

import kotlinx.serialization.Serializable

typealias OnAuthSuccess = (AuthMode) -> Unit

@Serializable
enum class AuthMode { LOGIN, REGISTER, FORGOT_PASSWORD, CHECK_MAIL, RESET_SUCCESS }
