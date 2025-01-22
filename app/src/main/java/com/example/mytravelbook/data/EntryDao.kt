package com.example.mytravelbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries WHERE tripId = :tripId")
    fun getEntriesByTripId(tripId: Int): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE id = :entryId LIMIT 1")
    suspend fun getEntryById(entryId: Int): Entry?

    /**
     * Insert and return the newly inserted row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntryReturnId(entry: Entry): Long

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("DELETE FROM entries WHERE tripId = :tripId")
    suspend fun deleteEntriesByTripId(tripId: Int)

}
