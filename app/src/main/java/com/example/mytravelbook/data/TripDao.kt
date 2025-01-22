package com.example.mytravelbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: Int): Trip?
}
