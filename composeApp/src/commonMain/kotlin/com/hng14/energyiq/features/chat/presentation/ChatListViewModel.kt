package com.hng14.energyiq.features.chat.presentation

import androidx.lifecycle.ViewModel
import com.hng14.energyiq.features.chat.data.ChatRepository
import com.hng14.energyiq.features.chat.domain.model.ChatCategory
import com.hng14.energyiq.features.chat.domain.model.ChatConversationSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChatListState(
    val conversations: List<ChatConversationSummary> = emptyList(),
    val selectedCategory: ChatCategory = ChatCategory.ALL,
    val searchQuery: String = ""
)

class ChatListViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()

    init {
        _state.update { it.copy(conversations = repository.conversations) }
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
