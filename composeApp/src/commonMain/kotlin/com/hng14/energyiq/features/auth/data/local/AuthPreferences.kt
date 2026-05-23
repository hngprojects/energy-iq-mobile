package com.hng14.energyiq.features.auth.data.local

import com.hng14.energyiq.core.storage.PreferenceStore

class AuthPreferences(private val store: PreferenceStore) {
    private companion object {
        const val AuthTokenKey = "auth_token"
        const val RefreshTokenKey = "refresh_token"
        const val UserIdKey = "user_id"
        const val PendingResetEmailKey = "pending_reset_email"
        const val PendingGoogleAuthModeKey = "pending_google_auth_mode"
        const val IsPersistentSessionKey = "is_persistent_session"
    }

    suspend fun getToken(): String? = store.get(AuthTokenKey)
    suspend fun getRefreshToken(): String? = store.get(RefreshTokenKey)
    suspend fun getUserId(): String? = store.get(UserIdKey)
    suspend fun getPendingResetEmail(): String? = store.get(PendingResetEmailKey)
    suspend fun getPendingGoogleAuthMode(): String? = store.get(PendingGoogleAuthModeKey)
    suspend fun isPersistentSession(): Boolean = store.get(IsPersistentSessionKey) == "true"

    suspend fun saveSession(token: String, refreshToken: String?, userId: String, isPersistent: Boolean = true) {
        store.put(AuthTokenKey, token)
        store.put(RefreshTokenKey, refreshToken)
        store.put(UserIdKey, userId)
        store.put(IsPersistentSessionKey, if (isPersistent) "true" else "false")
    }

    suspend fun saveTokens(token: String, refreshToken: String?) {
        store.put(AuthTokenKey, token)
        store.put(RefreshTokenKey, refreshToken)
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

    suspend fun clearAll() {
        store.clear()
    }

    suspend fun getUserScopedKey (key: String): String{
        val userId = getUserId() ?: "anonymous"
        return "$userId:$key"
    }
}
