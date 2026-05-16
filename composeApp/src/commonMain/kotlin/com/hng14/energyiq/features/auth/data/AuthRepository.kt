package com.hng14.energyiq.features.auth.data

import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.auth.data.local.UserDao
import com.hng14.energyiq.features.auth.data.local.UserEntity
import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordResponse
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ResetPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.VerifyEmailRequest
import com.hng14.energyiq.features.auth.domain.model.User

class AuthRepository(
    private val api: AuthApi,
    private val preferences: AuthPreferences,
    private val userDao: UserDao,
) {

    suspend fun login(email: String, password: String): User {
        val response = api.login(request = LoginRequest(email = email, password = password))
        val accessToken = response.data.accessToken
        val refreshToken = response.data.refreshToken
        val loginUser = response.data.user
        preferences.saveSession(token = accessToken, refreshToken = refreshToken, userId = loginUser.id)
        val remoteUser = api.me(token = accessToken).data
        val entity = UserEntity(
            id = remoteUser.id,
            email = remoteUser.email,
            name = "${remoteUser.firstName} ${remoteUser.lastName}",
        )
        userDao.upsert(user = entity)
        return entity.toDomain()
    }

    suspend fun signInWithAccessToken(accessToken: String): User {
        val remoteUser = api.me(token = accessToken).data
        preferences.saveSession(
            token = accessToken,
            refreshToken = null,
            userId = remoteUser.id,
        )
        val entity = UserEntity(
            id = remoteUser.id,
            email = remoteUser.email,
            name = "${remoteUser.firstName} ${remoteUser.lastName}",
        )
        userDao.upsert(user = entity)
        return entity.toDomain()
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User {
        val response = api.register(
            request = RegisterRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            ),
        )
        val remoteUser = response.data
        val entity = UserEntity(
            id = remoteUser.id,
            email = remoteUser.email,
            name = "${remoteUser.firstName} ${remoteUser.lastName}",
        )
        userDao.upsert(user = entity)
        preferences.saveUserId(entity.id)
        return entity.toDomain()
    }

    suspend fun forgotPassword(email: String): ForgotPasswordResponse {
        return api.forgotPassword(
            request = ForgotPasswordRequest(email = email),
        )
    }

    suspend fun resetPassword(email: String, password: String, token: String) {
        api.resetPassword(
            request = ResetPasswordRequest(
                email = email,
                password = password,
                token = token,
            ),
        )
    }

    suspend fun verifyEmail(email: String, otp: String): User {
        val response = api.verifyEmail(
            request = VerifyEmailRequest(
                email = email,
                otp = otp,
            ),
        )
        val accessToken = response.data.accessToken
        val refreshToken = response.data.refreshToken
        val verifiedUser = response.data.user
        preferences.saveSession(
            token = accessToken,
            refreshToken = refreshToken,
            userId = verifiedUser.id,
        )
        val entity = UserEntity(
            id = verifiedUser.id,
            email = verifiedUser.email,
            name = "${verifiedUser.firstName} ${verifiedUser.lastName}",
        )
        userDao.upsert(user = entity)
        return entity.toDomain()
    }

    suspend fun savePendingResetEmail(email: String) {
        preferences.savePendingResetEmail(email)
    }

    suspend fun getPendingResetEmail(): String? {
        return preferences.getPendingResetEmail()
    }

    suspend fun clearPendingResetEmail() {
        preferences.clearPendingResetEmail()
    }

    suspend fun getCurrentUser(): User? {
        val userId = preferences.getUserId() ?: return null
        return userDao.findById(id = userId)?.toDomain()
    }

    suspend fun logout() {
        val token = preferences.getToken() ?: throw Exception("You are not logged in")
        api.logout(token = token)
        preferences.clearSession()
        userDao.deleteAll()
    }

    private fun UserEntity.toDomain() = User(id = id, email = email, name = name)
}
