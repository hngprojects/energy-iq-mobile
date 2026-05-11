package com.hng14.energyiq.features.auth.data.local

import com.hng14.energyiq.core.storage.PreferenceStore

class AuthPreferences(private val store: PreferenceStore) {
    suspend fun getToken(): String? = store.get("auth_token")
    suspend fun getUserId(): String? = store.get("user_id")

    suspend fun saveSession(token: String, userId: String) {
        store.put("auth_token", token)
        store.put("user_id", userId)
    }


    /// temporary work around login endpoint is not working so we cant get token
    /// this will save session in room db and can be reused for login locally
    ///  TODO: remove this when login endpoint is working
    suspend fun saveUserId(userId: String) {
        store.put("user_id", userId)
    }

    suspend fun clearSession() {
        store.put("auth_token", null)
        store.put("user_id", null)
    }
}
