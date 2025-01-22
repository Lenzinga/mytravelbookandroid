package com.example.mytravelbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_state")
data class AppState(
    @PrimaryKey
    val id: Int = 0,
    val isFirstLaunch: Boolean
)
