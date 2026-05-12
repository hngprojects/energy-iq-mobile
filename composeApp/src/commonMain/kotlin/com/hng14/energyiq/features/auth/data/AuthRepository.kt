package com.hng14.energyiq.features.auth.data

import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.auth.data.local.UserDao
import com.hng14.energyiq.features.auth.data.local.UserEntity
import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import com.hng14.energyiq.features.auth.data.remote.dto.VerifyEmailRequest
import com.hng14.energyiq.features.auth.domain.model.User

class AuthRepository(
    private val api: AuthApi,
    private val preferences: AuthPreferences,
    private val userDao: UserDao,
) {

    suspend fun login(email: String, password: String): User {
        val response = api.login(request = LoginRequest(email = email, password = password))
        val remoteUser = response.data.user
        preferences.saveSession(token = response.data.accessToken, userId = remoteUser.id)
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

    suspend fun verifyEmail(email: String, otp: String) {
        api.verifyEmail(request = VerifyEmailRequest(email = email, otp = otp))
    }

    private fun UserEntity.toDomain() = User(id = id, email = email, name = name)
}
