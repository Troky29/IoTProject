package com.example.iotproject

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainActivityViewModel : ViewModel() {

    //lateinit var gates: List<Gate>
    //lateinit var activities: List<Activity>

    private val gates: MutableLiveData<List<Gate>> by lazy {
        MutableLiveData<List<Gate>>().also {
            loadGates()
        }
    }

    fun getGates(): LiveData<List<Gate>> {
        return gates
    }

    private fun loadGates() {
        //TODO: Code to import gate info from server, to do while waiting during login
    }

    fun updateLocation(location: Location?) {

    }

    data class Gate (val id: String)

    //data class Activity(val id: String)
}
