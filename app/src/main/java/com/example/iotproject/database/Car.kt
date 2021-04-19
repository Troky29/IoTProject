package com.example.iotproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car")
data class Car(
        @PrimaryKey val license: String,
        val color: String,
        val brand: String,
        val isGuest: Boolean,
        val nickname: String?,
        val deadline: String?,
        val imageURL: String?
)

