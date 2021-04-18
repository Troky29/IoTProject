package com.example.iotproject

import android.app.Application

class IoTApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.gateDao(), database.activityDao(), database.carDao()) }
    //TODO: move here the access token repo, maybe
    //val accessRepository by lazy { AccessTokenRepository }
}