package com.example.iotproject.fragments.car

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotproject.AccessTokenAuthenticator
import com.example.iotproject.AccessTokenInterceptor
import com.example.iotproject.AccessTokenRepository
import com.example.iotproject.Constants
import com.example.iotproject.database.AppRepository
import com.example.iotproject.database.Car
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CarFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    val TAG = "CarFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val carList: LiveData<List<Car>> = repository.allCars

    //TODO: eventually add also a load cars to get all car information
    fun loadCars() {}

    //This adds a car owned by the admin
    fun addCar(license: String, color: String, brand: String) {
        val body = """{"license":"$license", "color":"$color", "brand":"$brand"}""".trimMargin()
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "car")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for POST car")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added car!")
                        insert(Car(license, color, brand, false, null, null, null))
                    }
                    400 -> {
                        Log.e(TAG, "Error in POST, invalid car input data")
                        message.postValue(Constants.invalid_data)
                    }
                    409 -> {
                        Log.i(TAG, "Error in POST, car already exists")
                        message.postValue("Car already exists!")
                    }
                }
            }
        })
    }

    //This adds a temporary permit to a specific car
    fun addSpecialRule(nickname: String, license: String, color: String, brand: String, datetime: String) {
        val body = """{"nickname":"$nickname", "license":"$license", 
            |"color":"$color", "brand":"$brand", "dead_line":"$datetime"}""".trimMargin()
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "guest")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for POST guest rule")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added rule!")
                        insert(Car(license, color, brand, true, nickname, datetime, null))
                    }
                    400 -> {
                        Log.e(TAG, "Error in POST, invalid special rule input data")
                        message.postValue(Constants.invalid_data)
                    }
                    500 -> {
                        Log.e(TAG, "Server failed POST special rule")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }

    private fun insert(car: Car) = viewModelScope.launch {
        repository.insertCar(car)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}