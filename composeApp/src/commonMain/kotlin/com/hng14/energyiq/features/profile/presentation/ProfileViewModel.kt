package com.hng14.energyiq.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.auth.domain.model.User
import com.hng14.energyiq.features.profile.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val uploadedProfileUrl: String? = null,
    val successMessage: String? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val repository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            // Ensure we are not stuck in a logging out state
            _state.update { it.copy(isLoggingOut = false) }
            
            // Just use the cached profile from login
            val cachedUser = repository.getCurrentUser()
            if (cachedUser != null) {
                _state.update { it.copy(user = cachedUser, isLoading = false, error = null) }
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }
            runCatching {
                repository.logout()
            }.onSuccess {
                _state.update { it.copy(isLoggingOut = false) }
                onSuccess()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message, isLoggingOut = false) }
            }
        }
    }

    fun onDismissError() {
        _state.update { it.copy(error = null) }
    }

    fun onDismissSuccess() {
        _state.update { it.copy(successMessage = null) }
    }

    fun savePersonalSettings(
        fullName: String,
        businessName: String,
        businessType: String,
        state: String,
        city: String,
        aiLanguage: String = "English",
        profileUrl: String? = null,
    ) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val trimmed = fullName.trim()
            val firstName = trimmed.substringBefore(" ").trim().ifBlank { trimmed }
            val lastName = trimmed.substringAfter(" ", "").trim()

            runCatching {
                profileRepository.updatePersonalSettings(
                    firstName = firstName,
                    lastName = lastName,
                    profileUrl = profileUrl,
                    businessName = businessName.trim(),
                    businessType = businessType.trim(),
                    state = state.trim(),
                    city = city.trim(),
                    aiLanguage = aiLanguage.trim().ifBlank { "English" },
                )
            }.onSuccess { updatedUser ->
                _state.update {
                    it.copy(
                        user = updatedUser,
                        isSaving = false,
                        error = null,
                        successMessage = "Profile updated!",
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message ?: "Unable to save changes") }
            }
        }
    }

    fun uploadProfilePhoto(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ) {
        if (_state.value.isUploadingPhoto) return
        viewModelScope.launch {
            _state.update { it.copy(isUploadingPhoto = true, error = null) }
            runCatching {
                profileRepository.uploadProfileImage(bytes = bytes, fileName = fileName, mimeType = mimeType)
            }.onSuccess { url ->
                _state.update {
                    it.copy(
                        isUploadingPhoto = false,
                        uploadedProfileUrl = url,
                        user = it.user?.copy(profileUrl = url)
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isUploadingPhoto = false,
                        error = e.message ?: "Failed to upload photo"
                    )
                }
            }
        }
    }
}
