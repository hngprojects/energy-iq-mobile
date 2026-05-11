package com.hng14.energyiq.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.features.auth.data.AuthRepository
import com.hng14.energyiq.features.home.OnLogout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            _state.update { it.copy(user = user, isLoading = false) }
        }
    }

    fun onLogout(onLogout: OnLogout) {
        if (_state.value.isLoggingOut) return
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true, errorMessage = null) }
            runCatching {
                repository.logout()
            }.onSuccess {
                onLogout()
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.message ?: "Unable to sign out. Please try again.",
                    )
                }
            }
            _state.update { it.copy(isLoggingOut = false) }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
