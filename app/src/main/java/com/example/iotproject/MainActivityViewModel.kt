package com.example.iotproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.invalid_data
import com.example.iotproject.Constants.Companion.server_error
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

    //TODO: move this in the view model of the more fragment, since we have many operation to deal with
    fun addCar(license: String, color: String, brand: String) {
        val body = """{"license":"$license", "color":"$color", "brand":"$brand"}"""
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
                    400 -> message.postValue(invalid_data)
                    409 -> message.postValue("Car already exists!")
                }
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }

    //TODO:make this in their own classes
    data class Car(val license: String, val color: String, val brand: String)

}
/*
class MainActivityViewModelFactory(private val accessRepository: AccessTokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(accessRepository) as T
    }
}
*/