package com.example.iotproject

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception


class MainActivityViewModel(val accessRepository: AccessTokenRepository) : ViewModel() {

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(accessRepository))
            .addInterceptor(AccessTokenInterceptor(accessRepository))
            .build()

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

    fun loadGates() {
        val request = Request.Builder()
                .url(URL + "gate")
                .addHeader("x-access-token", accessRepository.token)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> try {
                        val json = JSONArray(response.body!!.string())
                        val list = ArrayList<Gate>()
                        for (index in 0 until json.length()) {
                            //TODO: update with correct data, also is there a more compact way?
                            val item = json.getJSONObject(index)
                            val name = item.get("name").toString()
                            val location = item.get("location").toString()
                            val state = item.get("state").toString()
                            val id = item.get("id").toString()
                            list.add(Gate(name, location, state, id))
                        }
                        gates.postValue(list)
                    } catch (e: Exception) {
                        message.postValue(server_error)
                    }
                    400 -> message.postValue(server_error)
                    404 -> message.postValue(no_gates)
                }
            }
        })
        //TODO: Code to import gate info from server, to do while waiting during login
    }

    private fun loadActivities() {
        //TODO: Code to import activities information, to do while logging in
    }

    fun getGates(): LiveData<List<Gate>> {
        return gates
    }

    fun getActivities(): LiveData<List<Activity>> {
        return activities
    }

    fun updateLocation(location: Location?) {
        //TODO: Upload user location every interval sec
        //message.postValue("Updated location")
        //message.postValue(location.toString())
        Log.i("MainActivityViewModel", "location received")
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("MainActivityViewModel", "Login destroyed")
    }

    data class Gate (val name: String, val location: String, val state: String, val id: String)

    data class Activity(val id: String)
}

class MainActivityViewModelFactory(val accessRepository: AccessTokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(accessRepository) as T
    }
}