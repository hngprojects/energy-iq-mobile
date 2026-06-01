package com.hng14.energyiq.features.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hng14.energyiq.core.socket.AgentSocket
import com.hng14.energyiq.core.socket.SocketConnectionState
import com.hng14.energyiq.features.auth.data.local.AuthPreferences
import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.domain.model.ChatCategory
import com.hng14.energyiq.features.chat.domain.model.ChatConversationSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListState(
    val conversations: List<ChatConversationSummary> = emptyList(),
    val selectedCategory: ChatCategory = ChatCategory.ALL,
    val searchQuery: String = "",
    val socketState: SocketConnectionState = SocketConnectionState.Disconnected,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class ChatListViewModel(
    private val repository: ChatRepository,
    private val agentSocket: AgentSocket,
    private val authPreferences: AuthPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()

    init {
        viewModelScope.launch { agentSocket.connect() }
        viewModelScope.launch { refreshChats() }

        viewModelScope.launch {
            agentSocket.connectionState.collect { s ->
                _state.update { it.copy(socketState = s) }
                if (s is SocketConnectionState.Connected) {
                    val userId = authPreferences.getUserId()?.trim().orEmpty()
                    if (userId.isNotBlank()) {
                        agentSocket.joinActiveChats(userId)
                    }
                }
            }
        }

        viewModelScope.launch {
            agentSocket.joinedChats.collect { evt ->
                // When the socket reports joined chats, re-fetch the canonical list via REST.
                refreshChats()
            }
        }

        viewModelScope.launch {
            agentSocket.errors.collect { err ->
                _state.update { it.copy(error = "${err.code}: ${err.message}", isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { refreshChats() }
    }

    private suspend fun refreshChats() {
        println("ChatList: refreshChats() start")
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repository.getChats() }
            .onSuccess { chats ->
                println("ChatList: refreshChats() success count=${chats.size}")
                _state.update { it.copy(conversations = chats, isLoading = false, error = null) }
            }
            .onFailure { e ->
                println("ChatList: refreshChats() failure error=${e.message}")
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load chats") }
            }
    }

    fun onCategorySelected(category: ChatCategory) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    val filteredConversations: List<ChatConversationSummary>
        get() {
            val query = _state.value.searchQuery.lowercase()
            val category = _state.value.selectedCategory
            return _state.value.conversations.filter {
                (category == ChatCategory.ALL || it.category == category) &&
                (query.isBlank() || it.title.lowercase().contains(query) || it.subtitle.lowercase().contains(query))
            }
        }
}
