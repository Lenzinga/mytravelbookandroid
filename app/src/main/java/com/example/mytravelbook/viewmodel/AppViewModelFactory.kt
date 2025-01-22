package com.example.mytravelbook.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytravelbook.data.AppDatabase
import com.example.mytravelbook.repository.EntryRepository
import com.example.mytravelbook.repository.ImageRepository
import com.example.mytravelbook.repository.TripRepository

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)

        val tripRepository = TripRepository(
            tripDao = database.tripDao(),
            imageDao = database.imageDao(),
            entryDao = database.entryDao()
        )
        val entryRepository = EntryRepository(database.entryDao())
        val imageRepository = ImageRepository(database.imageDao())

        return when {
            modelClass.isAssignableFrom(TripViewModel::class.java) -> {
                TripViewModel(tripRepository) as T
            }
            modelClass.isAssignableFrom(EntryViewModel::class.java) -> {
                EntryViewModel(entryRepository, imageRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}

