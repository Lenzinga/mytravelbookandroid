package com.example.mytravelbook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Trip::class, Entry::class, Image::class, AppState::class], // NEW: add AppState
    version = 2, // increment if you already had version=1
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun entryDao(): EntryDao
    abstract fun imageDao(): ImageDao

    // NEW: For storing app states
    abstract fun appStateDao(): AppStateDao

    // In AppDatabase.kt
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_travel_book_database"
                )
                    // 1) Add the fallback line:
                    .fallbackToDestructiveMigration()
                    // 2) Then build
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}
