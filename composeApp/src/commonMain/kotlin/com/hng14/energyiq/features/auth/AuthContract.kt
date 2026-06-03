package com.hng14.energyiq.features.auth

import kotlinx.serialization.Serializable

import com.hng14.energyiq.features.auth.domain.model.User

typealias OnAuthSuccess = (AuthMode, User) -> Unit

@Serializable
enum class AuthMode { LOGIN, REGISTER, FORGOT_PASSWORD, EMAIL_SENT, CHECK_MAIL, RESET_SUCCESS }
