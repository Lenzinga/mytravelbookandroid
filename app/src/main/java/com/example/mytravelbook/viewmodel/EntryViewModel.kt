package com.example.mytravelbook.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravelbook.data.Entry
import com.example.mytravelbook.data.Image
import com.example.mytravelbook.repository.EntryRepository
import com.example.mytravelbook.repository.ImageRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class EntryViewModel(
    private val entryRepository: EntryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _selectedTripId = MutableStateFlow<Int?>(null)
    val selectedTripId: StateFlow<Int?> = _selectedTripId.asStateFlow()

    val entriesForSelectedTrip: StateFlow<List<Entry>> =
        _selectedTripId
            .filterNotNull()
            .flatMapLatest { tripId ->
                entryRepository.getEntriesByTripId(tripId)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // For publish errors
    private val _publishError = MutableStateFlow<String?>(null)
    val publishError: StateFlow<String?> = _publishError.asStateFlow()

    fun clearPublishError() {
        _publishError.value = null
    }

    // ------------------------------------------------------------------------
    // Existing Methods
    // ------------------------------------------------------------------------

    fun setSelectedTrip(tripId: Int) {
        _selectedTripId.value = tripId
    }

    suspend fun getEntryById(entryId: Int): Entry? {
        return entryRepository.getEntryById(entryId)
    }

    suspend fun insertEntryAndReturnId(
        tripId: Int,
        title: String,
        text: String,
        location: String?
    ): Long {
        return entryRepository.insertEntryAndReturnId(tripId, title, text, location)
    }

    fun addEntry(title: String, text: String, location: String?) {
        val tripId = _selectedTripId.value ?: return
        viewModelScope.launch {
            entryRepository.insertEntry(tripId, title, text, location)
        }
    }

    fun updateEntry(entry: Entry) {
        viewModelScope.launch {
            entryRepository.updateEntry(entry)
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            entryRepository.deleteEntry(entry)
        }
    }

    fun getImagesForEntry(entryId: Int): Flow<List<Image>> {
        return imageRepository.getImagesForEntry(entryId)
    }

    fun addImage(entryId: Int, imageUri: String) {
        viewModelScope.launch {
            imageRepository.addImage(entryId, imageUri)
        }
    }

    fun deleteImage(image: Image) {
        viewModelScope.launch {
            imageRepository.deleteImage(image)
        }
    }

    // ------------------------------------------------------------------------
    // NEW: Publish with Base64 conversion
    // ------------------------------------------------------------------------
    fun publishEntry(context: Context, entry: Entry) {
        viewModelScope.launch {
            _publishError.value = null

            // 1) get URIs from DB for this entry
            val dbImages = imageRepository.getImagesForEntry(entry.id)
                .firstOrNull()
                ?.map { it.imageUri }
                ?: emptyList()

            // 2) Convert each URI -> Base64
            val base64Images = dbImages.mapNotNull { uriString ->
                val uri = Uri.parse(uriString)
                try {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val bytes = input.readBytes()
                        Base64.encodeToString(bytes, Base64.DEFAULT)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            // 3) Actually publish
            try {
                entryRepository.publishEntry(entry, base64Images)
            } catch (e: Exception) {
                _publishError.value = "Failed to publish entry \"${entry.title}\""
            }
        }
    }
}
