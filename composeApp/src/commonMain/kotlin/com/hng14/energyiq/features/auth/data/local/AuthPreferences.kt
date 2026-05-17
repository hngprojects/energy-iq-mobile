package com.hng14.energyiq.features.auth.data.local

import com.hng14.energyiq.core.storage.PreferenceStore

class AuthPreferences(private val store: PreferenceStore) {
    private companion object {
        const val AuthTokenKey = "auth_token"
        const val RefreshTokenKey = "refresh_token"
        const val UserIdKey = "user_id"
        const val PendingResetEmailKey = "pending_reset_email"
        const val PendingGoogleAuthModeKey = "pending_google_auth_mode"
    }

    suspend fun getToken(): String? = store.get(AuthTokenKey)
    suspend fun getRefreshToken(): String? = store.get(RefreshTokenKey)
    suspend fun getUserId(): String? = store.get(UserIdKey)
    suspend fun getPendingResetEmail(): String? = store.get(PendingResetEmailKey)
    suspend fun getPendingGoogleAuthMode(): String? = store.get(PendingGoogleAuthModeKey)

    suspend fun saveSession(token: String, refreshToken: String?, userId: String) {
        store.put(AuthTokenKey, token)
        store.put(RefreshTokenKey, refreshToken)
        store.put(UserIdKey, userId)
    }

    suspend fun saveTokens(token: String, refreshToken: String?) {
        store.put(AuthTokenKey, token)
        store.put(RefreshTokenKey, refreshToken)
    }


    /// temporary work around login endpoint is not working so we cant get token
    /// this will save session in room db and can be reused for login locally
    ///  TODO: remove this when login endpoint is working
    suspend fun saveUserId(userId: String) {
        store.put(UserIdKey, userId)
    }

    suspend fun savePendingResetEmail(email: String) {
        store.put(PendingResetEmailKey, email)
    }

    suspend fun savePendingGoogleAuthMode(mode: String?) {
        store.put(PendingGoogleAuthModeKey, mode)
    }

    suspend fun clearPendingResetEmail() {
        store.put(PendingResetEmailKey, null)
    }

    suspend fun clearPendingGoogleAuthMode() {
        store.put(PendingGoogleAuthModeKey, null)
    }

    suspend fun clearSession() {
        store.put(AuthTokenKey, null)
        store.put(RefreshTokenKey, null)
        store.put(UserIdKey, null)
        clearPendingResetEmail()
        clearPendingGoogleAuthMode()
    }
}
