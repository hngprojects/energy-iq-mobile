package com.hng14.energyiq.features.profile.data

import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.core.network.CloudinaryConfig
import com.hng14.energyiq.features.profile.data.remote.CloudinaryApi
import com.hng14.energyiq.features.profile.data.remote.ProfileApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ProfileRepository(
    private val api: ProfileApi,
    private val cloudinaryApi: CloudinaryApi,
    private val authRepository: AuthRepository,
) {
    suspend fun uploadProfileImage(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): String {
        // 1. Upload to the custom backend
        val uploadedUrl = api.uploadProfileImage(
            bytes = bytes,
            fileName = fileName,
            mimeType = mimeType
        )

        // 2. Fetch the current logged-in user
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            // 3. Save the new image URL into local Room DB / Preferences cache
            val trimmed = currentUser.name.trim()
            val firstName = trimmed.substringBefore(" ").trim().ifBlank { trimmed }
            val lastName = trimmed.substringAfter(" ", "").trim()
            authRepository.updateLocalUser(
                id = currentUser.id,
                firstName = firstName,
                lastName = lastName,
                businessName = currentUser.businessName.orEmpty(),
                businessType = currentUser.businessType.orEmpty(),
                state = currentUser.state.orEmpty(),
                city = currentUser.city.orEmpty(),
                aiLanguage = currentUser.aiLanguage.orEmpty(),
                profileUrl = uploadedUrl
            )
        }

        return uploadedUrl
    }

    suspend fun updatePersonalSettingsRaw(body: Map<String, JsonElement>) {
        api.updatePersonalSettings(body)
    }

    suspend fun updatePersonalSettings(
        firstName: String,
        lastName: String,
        profileUrl: String?,
        businessName: String,
        businessType: String,
        state: String,
        city: String,
        aiLanguage: String,
    ): User {
        val body = buildJsonObject {
            put("firstName", firstName)
            if (lastName.isNotBlank()) put("lastName", lastName)
            if (!profileUrl.isNullOrBlank()) put("profileUrl", profileUrl)
            put("businessName", businessName)
            put("businessType", businessType)
            put("state", state)
            put("city", city)
            put("aiLanguage", aiLanguage)
        }

        api.updatePersonalSettings(body)

        val currentUser = authRepository.getCurrentUser() ?: throw Exception("User not found")

        return authRepository.updateLocalUser(
            id = currentUser.id,
            firstName = firstName,
            lastName = lastName,
            businessName = businessName,
            businessType = businessType,
            state = state,
            city = city,
            aiLanguage = aiLanguage,
            profileUrl = profileUrl
        )
    }
}
