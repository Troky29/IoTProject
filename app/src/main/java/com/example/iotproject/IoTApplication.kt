package com.example.iotproject

import android.app.Application
import com.example.iotproject.fragments.gate.GateDatabase
import com.example.iotproject.fragments.gate.GateRepository

class IoTApplication : Application() {
    val database by lazy { GateDatabase.getDatabase(this) }
    val repository by lazy { GateRepository(database.gateDao()) }
}