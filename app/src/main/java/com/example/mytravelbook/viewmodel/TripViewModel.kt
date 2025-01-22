package com.example.mytravelbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravelbook.data.Trip
import com.example.mytravelbook.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Responsible for managing trips (fetching, adding, editing, deleting).
 */
class TripViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    // Flow of all trips, exposed as a StateFlow (for Compose)
    val trips: StateFlow<List<Trip>> =
        tripRepository.allTrips
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Insert a new trip with the given name.
     */
    fun addTrip(name: String) {
        viewModelScope.launch {
            tripRepository.insertTrip(name)
        }
    }

    /**
     * Update an existing trip.
     * For example: rename or other modifications
     */
    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.updateTrip(trip)
        }
    }

    /**
     * Delete the provided trip.
     */
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.deleteTrip(trip)
        }
    }

    fun deleteTripWithEntries(trip: Trip) {
        viewModelScope.launch {
            tripRepository.deleteTripAndItsEntries(trip)
        }
    }

    suspend fun getTripById(tripId: Int): Trip? {
        return tripRepository.getTripById(tripId)
    }

}
