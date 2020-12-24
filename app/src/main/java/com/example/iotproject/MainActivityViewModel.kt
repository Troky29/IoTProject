package com.example.iotproject

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    lateinit var gates: List<Gate>
    lateinit var activities: List<Activity>

    fun updateGates() {
        //TODO: Code to import gate info from server, to do while waiting during login
    }

    data class Gate (val id: String)

    data class Activity(val id: String)
}