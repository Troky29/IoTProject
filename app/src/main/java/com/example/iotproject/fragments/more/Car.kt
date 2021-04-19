package com.example.iotproject.fragments.more

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car")
data class Car(
        @PrimaryKey val license: String,
        val color: String,
        val brand: String,
        val imageURL: String?
)

