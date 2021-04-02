package com.example.iotproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.no_gates
import com.example.iotproject.Constants.Companion.server_error
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MainActivityViewModel : ViewModel() {
    val TAG = "MainActivityViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
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
                .addHeader("x-access-token", AccessTokenRepository.token)
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

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }

    //TODO: move this in the view model of the more fragment, since we have many operation to deal with
    fun addCar(license: String, color: String, brand: String) {
        val body = """{"license":"$license","color":"$color", "brand":"$brand"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "car")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue("Successfully added car!")
                    400 -> message.postValue(Constants.invalid_data)
                    409 -> message.postValue("Car already exists!")
                }
            }
        })
    }

    //TODO:make this in their own classes
    data class Car(val license: String, val color: String, val brand: String)

    data class Gate(val name: String, val location: String, val state: String, val id: String)

    data class Activity(val id: String)
}
/*
class MainActivityViewModelFactory(private val accessRepository: AccessTokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(accessRepository) as T
    }
}
*/