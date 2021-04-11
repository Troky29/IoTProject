package com.example.iotproject.fragments.activity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.iotproject.fragments.gate.Gate

@Entity(tableName = "activity")
data class Activity(val gate: String, val datetime: String, var state: String, val image: String?) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}