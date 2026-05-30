package com.hng14.energyiq.features.auth.data.remote

import com.hng14.energyiq.features.auth.data.remote.dto.ApiErrorResponse

class AuthException(
    val errorResponse: ApiErrorResponse?,
    val httpStatus: Int? = null,
    message: String
) : Exception(message)
