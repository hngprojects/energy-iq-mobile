package dev.logickoder.starter.core.storage

import kotlinx.browser.localStorage

actual fun createPreferenceStore(context: Any?): PreferenceStore = LocalStoragePreferenceStore()

private class LocalStoragePreferenceStore : PreferenceStore {
    override suspend fun get(key: String): String? = localStorage.getItem(key)

    override suspend fun put(key: String, value: String?) {
        if (value != null) localStorage.setItem(key, value)
        else localStorage.removeItem(key)
    }
}
