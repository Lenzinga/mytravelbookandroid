package com.example.mytravelbook.repository

import com.example.mytravelbook.data.Trip
import com.example.mytravelbook.data.TripDao
import kotlinx.coroutines.flow.Flow
import com.example.mytravelbook.data.ImageDao
import com.example.mytravelbook.data.EntryDao

class TripRepository(private val tripDao: TripDao,
                     private val imageDao: ImageDao,
                     private val entryDao: EntryDao) {

    // Flow of all trips; collects from DB in real time
    val allTrips: Flow<List<Trip>> = tripDao.getAllTrips()

    suspend fun getTripById(tripId: Int): Trip? {
        return tripDao.getTripById(tripId)
    }

    suspend fun insertTrip(name: String) {
        val newTrip = Trip(
            name = name,
            createdAt = System.currentTimeMillis()
        )
        tripDao.insertTrip(newTrip)
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun deleteTripAndItsEntries(trip: Trip) {
        // Delete images for entries in this trip
        imageDao.deleteImagesByTripId(trip.id)
        // Delete entries for this trip
        entryDao.deleteEntriesByTripId(trip.id)
        // delete the trip itself
        tripDao.deleteTrip(trip)
    }


}
