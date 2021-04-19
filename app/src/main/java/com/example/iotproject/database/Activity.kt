package com.example.iotproject.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "activity",
        foreignKeys = [
            ForeignKey(
                    entity = Gate::class,
                    parentColumns = ["code"],
                    childColumns = ["gate"]),
            ForeignKey(entity = Car::class,
                    parentColumns = ["license"],
                    childColumns = ["car"])])
data class Activity(
        @PrimaryKey(autoGenerate = true) val id: Int,
        val gate: String,
        val datetime: String,
        val state: String,
        val car: String,
        val imageURL: String?
)