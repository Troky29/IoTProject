package com.example.iotproject.fragments.activity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity")
data class Activity(val gate: String, val datetime: String, var state: String, val imageURL: String?) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}