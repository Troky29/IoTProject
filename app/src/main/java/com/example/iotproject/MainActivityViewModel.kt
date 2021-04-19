package com.example.iotproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.invalid_data
import com.example.iotproject.Constants.Companion.server_error
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class MainActivityViewModel : ViewModel() {
    val TAG = "MainActivityViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getUser() {
        //TODO: retrieves all information about the user, to be used for display purposes
    }

    fun logout() {
        //TODO: upon opening the user icon dialog you can see all the info and logout
    }

    //TODO: move this in the view model of the more fragment, since we have many operation to deal with
    fun addCar(license: String, color: String, brand: String) {
        val body = """{"license":"$license", "color":"$color", "brand":"$brand"}""".trimMargin()
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "car")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for POST car")
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added car!")
                    }
                    400 -> {
                        message.postValue(invalid_data)
                    }
                    409 -> {
                        message.postValue("Car already exists!")
                    }
                }
            }
        })
    }

    fun addSpecialRule(nickname: String, license: String, color: String, brand: String, datetime: String) {
        val body = """{"nickname":"$nickname", "license":"$license", 
            |"color":"$color", "brand":"$brand", "dead_line":"$datetime"}""".trimMargin()
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "guest")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue("Successfully added rule!")
                    400 -> message.postValue(invalid_data)
                    500 -> message.postValue(server_error)
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}