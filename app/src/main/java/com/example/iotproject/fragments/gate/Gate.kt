package com.example.iotproject.fragments.gate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gate")
data class Gate(val name: String, val location: String, val code: String, val imageURL: String?) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
