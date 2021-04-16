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
        @PrimaryKey(autoGenerate = true) val id: Int,
        val gate: String,
        val datetime: String,
        val state: String,
        val imageURL: String?,
)