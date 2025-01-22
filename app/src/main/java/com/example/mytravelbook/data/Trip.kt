package com.example.mytravelbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long // store as a timestamp (System.currentTimeMillis())
)
