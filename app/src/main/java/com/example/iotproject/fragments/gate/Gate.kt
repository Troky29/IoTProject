package com.example.iotproject.fragments.gate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gate")
data class Gate(
        @PrimaryKey val code: String,
        val name: String,
        val location: String,
        val imageURL: String?
)
