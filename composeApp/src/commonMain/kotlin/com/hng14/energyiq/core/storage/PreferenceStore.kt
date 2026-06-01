package com.hng14.energyiq.core.storage

interface PreferenceStore {
    suspend fun get(key: String): String?
    suspend fun put(key: String, value: String?)
    suspend fun clear()
}

expect fun createPreferenceStore(context: Any? = null): PreferenceStore
