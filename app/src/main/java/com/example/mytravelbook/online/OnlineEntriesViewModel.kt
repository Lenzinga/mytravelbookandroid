package com.example.mytravelbook.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for loading diaries from the backend.
 */
class OnlineEntriesViewModel(
    private val repository: OnlineEntriesRepository = OnlineEntriesRepository()
) : ViewModel() {

    private val _onlineEntries = MutableStateFlow<List<RemoteDiaryEntry>>(emptyList())
    val onlineEntries = _onlineEntries.asStateFlow()

    // For showing detail
    private val _selectedOnlineEntry = MutableStateFlow<RemoteDiaryEntry?>(null)
    val selectedOnlineEntry = _selectedOnlineEntry.asStateFlow()

    /**
     * Load all diaries from the backend.
     */
    fun loadAllOnlineEntries() {
        viewModelScope.launch {
            try {
                val entries = repository.getAllOnlineEntries()
                _onlineEntries.value = entries
            } catch (e: Exception) {
                // handle error, or set an error state
                _onlineEntries.value = emptyList()
            }
        }
    }

    /**
     * Load a single entry by ID from the backend.
     */
    fun loadOnlineEntryById(diaryId: String) {
        viewModelScope.launch {
            try {
                val entry = repository.getOnlineEntryById(diaryId)
                _selectedOnlineEntry.value = entry
            } catch (e: Exception) {
                _selectedOnlineEntry.value = null
            }
        }
    }

    fun clearSelectedEntry() {
        _selectedOnlineEntry.value = null
    }
}
