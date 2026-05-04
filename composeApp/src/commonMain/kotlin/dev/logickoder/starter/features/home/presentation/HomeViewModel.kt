package dev.logickoder.starter.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.logickoder.starter.features.auth.data.AuthRepository
import dev.logickoder.starter.features.home.OnLogout
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
        viewModelScope.launch {
            repository.logout()
            onLogout()
        }
    }
}
