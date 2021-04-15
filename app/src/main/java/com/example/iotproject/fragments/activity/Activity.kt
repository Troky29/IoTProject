package com.example.iotproject.fragments.activity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.iotproject.fragments.gate.Gate

@Entity(tableName = "activity",
        foreignKeys = [ForeignKey(entity = Gate::class,
                parentColumns = ["code"],
                childColumns = ["gate"])])
data class Activity(
        val gate: String,
        val datetime: String,
        var state: String,
        val imageURL: String?,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}