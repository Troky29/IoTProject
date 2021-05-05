package com.example.iotproject

import android.app.Application
import com.example.iotproject.database.AppDatabase
import com.example.iotproject.database.AppRepository

class IoTApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.gateDao(), database.activityDao(), database.carDao()) }
}