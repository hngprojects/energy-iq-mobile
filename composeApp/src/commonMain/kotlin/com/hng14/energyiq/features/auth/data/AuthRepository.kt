package com.hng14.energyiq.features.auth.data

import com.hng14.energyiq.features.auth.AuthMode
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.auth.data.local.UserDao
import com.hng14.energyiq.features.auth.data.local.UserEntity
import com.hng14.energyiq.features.auth.data.remote.AuthApi
import com.hng14.energyiq.features.auth.data.remote.AuthException
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ForgotPasswordResponse
import com.hng14.energyiq.features.auth.data.remote.dto.GoogleMobileRequest
import com.hng14.energyiq.features.auth.data.remote.dto.LoginRequest
import com.hng14.energyiq.features.auth.data.remote.dto.RegisterRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ResendEmailOtpRequest
import com.hng14.energyiq.features.auth.data.remote.dto.ResetPasswordRequest
import com.hng14.energyiq.features.auth.data.remote.dto.VerifyEmailRequest
import com.hng14.energyiq.features.auth.data.remote.dto.UserDto
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.features.onboarding.data.OnboardingPreferences

class AuthRepository(
    private val api: AuthApi,
    private val preferences: AuthPreferences,
    private val userDao: UserDao,
    private val onboardingPreferences: OnboardingPreferences,
) {

    suspend fun signInWithGoogleIdToken(idToken: String, isRememberMe: Boolean = true): User {
        preferences.clearAuthData()

        val response = api.googleMobileSignIn(
            request = GoogleMobileRequest(idToken = idToken),
        )

        val accessToken = response.data.accessToken
        val refreshToken = response.data.refreshToken
        val loginUser = response.data.user

        preferences.saveSession(
            token = accessToken,
            refreshToken = refreshToken,
            userId = loginUser.id,
            isPersistent = isRememberMe,
        )

        val remoteUser = api.me(token = accessToken).data
        val entity = upsertRemoteUser(remoteUser)
        return entity.toDomain()
    }

    suspend fun login(email: String, password: String, isRememberMe: Boolean = true): User {
        // Clear tokens but keep the identity if we need to check local state before overwrite
        preferences.clearAuthData()
        
        val response = try {
            api.login(request = LoginRequest(email = email, password = password))
        } catch (e: AuthException) {
            val status = e.errorResponse?.statusCode ?: e.httpStatus
            if (status == 401 || status == 403) throw UnverifiedEmailException(email = email)
            throw e
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            if (msg.contains("not verified", ignoreCase = true)) {
                throw UnverifiedEmailException(email = email)
            }
            throw e
        }
        
        val accessToken = response.data.accessToken
        val refreshToken = response.data.refreshToken
        val loginUser = response.data.user
        
        preferences.saveSession(
            token = accessToken, 
            refreshToken = refreshToken, 
            userId = loginUser.id,
            isPersistent = isRememberMe
        )
        
        val remoteUser = try {
            api.me(token = accessToken).data
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            if (msg.contains("not verified", ignoreCase = true)) {
                preferences.clearSession()
                throw UnverifiedEmailException(email = email)
            }
            throw e
        }
        val entity = upsertRemoteUser(remoteUser)
        return entity.toDomain()
    }

    suspend fun signInWithAccessToken(accessToken: String, isRememberMe: Boolean = true): User {
        preferences.clearAuthData()
        
        val remoteUser = api.me(token = accessToken).data
        preferences.saveSession(
            token = accessToken,
            refreshToken = null,
            userId = remoteUser.id,
            isPersistent = isRememberMe
        )
        
        return getMe()
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): User {
        // Important: ensure we don't keep a previous user's session while registering a new account.
        // The register endpoint typically does not return auth tokens, so leaving the old token in
        // storage would cause subsequent API calls (e.g. chats) to be made as the previous user.
        preferences.clearSession()

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
            role = remoteUser.role,
            emailVerified = remoteUser.emailVerified,
            onBoardingComplete = false,
            inverterBrand = null,
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
        
        val remoteUser = api.me(token = accessToken).data
        val entity = upsertRemoteUser(remoteUser)
        // Force emailVerified to true since we just verified it
        val forcedEntity = entity.copy(emailVerified = true)
        userDao.upsert(forcedEntity)
        
        return forcedEntity.toDomain()
    }

    suspend fun resendEmailOtp(email: String): String {
        val response = api.resendEmailOtp(
            request = ResendEmailOtpRequest(email = email.trim()),
        )
        return response.message
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

    suspend fun savePendingGoogleAuthMode(mode: AuthMode) {
        preferences.savePendingGoogleAuthMode(mode.name)
    }

    suspend fun getPendingGoogleAuthMode(): AuthMode? {
        val rawMode = preferences.getPendingGoogleAuthMode() ?: return null
        return runCatching { AuthMode.valueOf(rawMode) }.getOrNull()
    }

    suspend fun clearPendingGoogleAuthMode() {
        preferences.clearPendingGoogleAuthMode()
    }

    suspend fun getCurrentUser(): User? {
        val isPersistent = preferences.isPersistentSession()
        val userId = preferences.getUserId() ?: return null
        
        if (!isPersistent) {
            logout()
            return null
        }

        val entity = userDao.findById(id = userId) ?: return null

        // Onboarding completion is stored in user-scoped preferences during inverter setup.
        // Persist it back into the local user cache so cold-start routing is correct.
        val onboardingComplete = onboardingPreferences.isComplete() || (entity.onBoardingComplete == true)
        if (onboardingComplete && entity.onBoardingComplete != true) {
            userDao.upsert(entity.copy(onBoardingComplete = true))
        }

        return entity.toDomain().copy(onBoardingComplete = onboardingComplete)
    }

    suspend fun logout() {
        runCatching {
            val token = preferences.getToken()
            if (token != null) {
                api.logout(token = token)
            }
        }
        preferences.clearSession()
    }

    suspend fun getMe(): User {
        val token = preferences.getToken() ?: throw Exception("Not logged in")
        val remoteUser = api.me(token = token).data
        val entity = upsertRemoteUser(remoteUser)
        return entity.toDomain()
    }

    suspend fun updateLocalUser(
        id: String,
        firstName: String,
        lastName: String,
        businessName: String,
        businessType: String,
        state: String,
        city: String,
        aiLanguage: String,
        profileUrl: String?
    ): User {
        val local = userDao.findById(id) ?: throw Exception("User not found locally")
        val updated = local.copy(
            name = "$firstName $lastName".trim(),
            businessName = businessName,
            businessType = businessType,
            state = state,
            city = city,
            aiLanguage = aiLanguage,
            profileUrl = profileUrl ?: local.profileUrl
        )
        userDao.upsert(updated)
        return updated.toDomain()
    }

    private suspend fun upsertRemoteUser(remoteUser: UserDto): UserEntity {
        val localUser = userDao.findById(remoteUser.id)
        val isLocallyOnboarded = onboardingPreferences.isComplete()

        val entity = UserEntity(
            id = remoteUser.id,
            email = remoteUser.email,
            name = "${remoteUser.firstName} ${remoteUser.lastName}",
            role = remoteUser.role,
            emailVerified = localUser?.emailVerified ?: remoteUser.emailVerified,
            onBoardingComplete = isLocallyOnboarded ||
                    (localUser?.onBoardingComplete == true) ||
                    (remoteUser.onboardingComplete == true),
            inverterBrand = remoteUser.inverterBrand?.takeIf { it.isNotBlank() } ?: localUser?.inverterBrand,
            businessName = remoteUser.businessName?.takeIf { it.isNotBlank() } ?: localUser?.businessName,
            businessType = remoteUser.businessType?.takeIf { it.isNotBlank() } ?: localUser?.businessType,
            state = remoteUser.state?.takeIf { it.isNotBlank() } ?: localUser?.state,
            city = remoteUser.city?.takeIf { it.isNotBlank() } ?: localUser?.city,
            aiLanguage = remoteUser.aiLanguage?.takeIf { it.isNotBlank() } ?: localUser?.aiLanguage,
            profileUrl = remoteUser.profileUrl?.takeIf { it.isNotBlank() } ?: localUser?.profileUrl,
        )
        userDao.upsert(user = entity)
        return entity
    }

    private fun UserEntity.toDomain() = User(
        id = id,
        email = email,
        name = name,
        role  = role,
        emailVerified = emailVerified,
        onBoardingComplete = onBoardingComplete ?: false,
        inverterBrand = inverterBrand,
        businessName = businessName,
        businessType = businessType,
        state = state,
        city = city,
        aiLanguage = aiLanguage,
        profileUrl = profileUrl,
    )
}

class UnverifiedEmailException(val email: String) : Exception("Email not verified")
