package com.example.mytravelbook.online

class OnlineEntriesRepository {

    /**
     * Fetch all diaries from the backend.
     */
    suspend fun getAllOnlineEntries(): List<RemoteDiaryEntry> {
        return RemoteDiaryService.fetchAllDiaries()
    }

    /**
     * Fetch a single diary entry from the backend.
     */
    suspend fun getOnlineEntryById(diaryId: String): RemoteDiaryEntry {
        return RemoteDiaryService.fetchDiary(diaryId)
    }
}
