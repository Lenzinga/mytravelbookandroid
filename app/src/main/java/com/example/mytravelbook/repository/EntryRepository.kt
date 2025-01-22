package com.example.mytravelbook.repository

import com.example.mytravelbook.data.Entry
import com.example.mytravelbook.data.EntryDao
import com.example.mytravelbook.online.EntryPublisher
import kotlinx.coroutines.flow.Flow

class EntryRepository(private val entryDao: EntryDao) {

    fun getEntriesByTripId(tripId: Int): Flow<List<Entry>> {
        return entryDao.getEntriesByTripId(tripId)
    }

    suspend fun getEntryById(entryId: Int): Entry? {
        return entryDao.getEntryById(entryId)
    }

    suspend fun insertEntry(tripId: Int, title: String, text: String, location: String?) {
        val newEntry = Entry(
            tripId = tripId,
            title = title,
            text = text,
            location = location,
            timestamp = System.currentTimeMillis(),
            isPublished = false
        )
        entryDao.insertEntryReturnId(newEntry)
    }

    suspend fun insertEntryAndReturnId(
        tripId: Int,
        title: String,
        text: String,
        location: String?
    ): Long {
        val newEntry = Entry(
            tripId = tripId,
            title = title,
            text = text,
            location = location,
            timestamp = System.currentTimeMillis(),
            isPublished = false
        )
        return entryDao.insertEntryReturnId(newEntry)
    }

    suspend fun updateEntry(entry: Entry) {
        entryDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: Entry) {
        entryDao.deleteEntry(entry)
    }

    /**
     * Publish entry with Ktor:
     *  1) Attempt POST call
     *  2) If success, mark isPublished = true in local DB
     *  NOTE: 'dbImages' is a list of Base64 strings
     */
    suspend fun publishEntry(entry: Entry, dbImages: List<String>): Boolean {
        val remoteId = EntryPublisher.publishLocalEntryToBackend(entry, dbImages)
        return if (remoteId != null) {
            // success
            val updatedEntry = entry.copy(isPublished = true)
            entryDao.updateEntry(updatedEntry)
            true
        } else {
            false
        }
    }
}
