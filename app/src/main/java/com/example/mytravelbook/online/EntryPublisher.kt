package com.example.mytravelbook.online

import com.example.mytravelbook.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Functions for publishing a local Entry to the remote API using Ktor (RemoteDiaryService).
 * The 'dbImages' parameter must already be Base64-encoded strings for each image.
 */
object EntryPublisher {

    /**
     * Publishes a local [Entry] to the remote API by constructing a [CreateDiaryRequestBody].
     * Returns the new remote ID (UUID) if successful, or null if an error happens.
     */
    suspend fun publishLocalEntryToBackend(entry: Entry, dbImages: List<String>): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Convert the local Entry timestamp into an ISO8601 string with milliseconds
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val dateTimeString = sdf.format(Date(entry.timestamp))

                val body = CreateDiaryRequestBody(
                    title = entry.title.ifBlank { "Untitled" },
                    text = entry.text.ifBlank { "No text" },
                    locationName = entry.location,
                    images = dbImages,  // these are ALREADY base64
                    dateTime = dateTimeString
                )

                val response = RemoteDiaryService.createDiary(body)
                // Return the newly created remote ID from the server response
                response.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
