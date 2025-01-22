package com.example.mytravelbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tripId: Int,            // foreign key reference to Trip
    val title: String,
    val text: String,
    val location: String?,      // optional
    val timestamp: Long,        // automatically set when creating an Entry
    val isPublished: Boolean = false
)
