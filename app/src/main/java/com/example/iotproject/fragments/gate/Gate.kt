package com.example.iotproject.fragments.gate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gate")
data class Gate(val name: String,
                val location: String,
                @PrimaryKey val code: String,
                val imageURL: String?) {
}
