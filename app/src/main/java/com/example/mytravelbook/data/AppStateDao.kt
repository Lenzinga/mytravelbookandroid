package com.example.mytravelbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppStateDao {

    @Query("SELECT * FROM app_state WHERE id = 0 LIMIT 1")
    suspend fun getAppState(): AppState?

    // Overwrite if it already exists.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAppState(appState: AppState)
}
