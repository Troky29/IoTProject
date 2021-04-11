package com.example.iotproject

import android.app.Application

class IoTApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.gateDao(), database.activityDao()) }
}