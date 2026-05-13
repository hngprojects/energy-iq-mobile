package com.hng14.energyiq.core.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual fun createPreferenceStore(context: Any?): PreferenceStore {
    requireNotNull(context) { "Android context required" }
    return EncryptedPreferenceStore(context as Context)
}

private class EncryptedPreferenceStore(context: Context) : PreferenceStore {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "energy_iq_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override suspend fun get(key: String): String? = prefs.getString(key, null)

    override suspend fun put(key: String, value: String?) {
        val editor = prefs.edit()
        if (value != null) {
            editor.putString(key, value)
        } else {
            editor.remove(key)
        }
        editor.apply()
    }
}
