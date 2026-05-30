package com.hng14.energyiq.features.profile.data

import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.core.network.CloudinaryConfig
import com.hng14.energyiq.features.profile.data.remote.CloudinaryApi
import com.hng14.energyiq.features.profile.data.remote.ProfileApi

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
        val res = cloudinaryApi.uploadImage(
            cloudName = CloudinaryConfig.CLOUD_NAME,
            uploadPreset = CloudinaryConfig.UPLOAD_PRESET,
            bytes = bytes,
            fileName = fileName,
            mimeType = mimeType,
        )
        return res.secureUrl.ifBlank { res.url }
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
        val body = buildMap<String, String> {
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

        // Manually update local cache instead of relying on getMe() which may have issues.
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
