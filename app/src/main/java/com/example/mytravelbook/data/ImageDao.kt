package com.example.mytravelbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM images WHERE entryId = :entryId")
    fun getImagesForEntry(entryId: Int): Flow<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("""
    DELETE FROM images 
    WHERE entryId IN (
       SELECT id FROM entries WHERE tripId = :tripId
    )
""")
    suspend fun deleteImagesByTripId(tripId: Int)

}
