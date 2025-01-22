package com.example.mytravelbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entryId: Int,      // foreign key reference to Entry
    val imageUri: String   // URI or path stored as a String
)
