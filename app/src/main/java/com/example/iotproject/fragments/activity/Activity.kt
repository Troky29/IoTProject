package com.example.iotproject.fragments.activity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.iotproject.fragments.gate.Gate
import com.example.iotproject.fragments.more.Car

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