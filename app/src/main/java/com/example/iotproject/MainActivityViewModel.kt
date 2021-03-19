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

    private val activities: MutableLiveData<List<Activity>> by lazy {
        MutableLiveData<List<Activity>>().also {
            loadActivities()
        }
    }

    private fun loadGates() {
        //TODO: Code to import gate info from server, to do while waiting during login
    }

    private fun loadActivities() {
        //TODO: Code to import activities inforation, to do while logging in
    }

    fun getGates(): LiveData<List<Gate>> {
        return gates
    }

    fun getActivities(): LiveData<List<Activity>> {
        return activities
    }

    fun updateLocation(location: Location?) {
        //TODO: Upload user location every interval sec
    }

    data class Gate (val id: String)

    data class Activity(val id: String)
}
