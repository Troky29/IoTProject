package com.example.iotproject.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.Constants
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.invalid_data
import com.example.iotproject.Constants.Companion.invalid_user
import com.example.iotproject.Constants.Companion.server_error
import com.example.iotproject.Constants.Companion.success
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginViewModel : ViewModel() {
    val TAG = "LoginViewModel"

    private val client = OkHttpClient()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val token: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val refreshToken: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getSessionToken(jwtRefresh: String)  {
        val body = """{"jwt_refresh":"$jwtRefresh"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "jwt")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for POST session token")
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> try {
                        val jwtExpiry = JSONObject(response.body!!.string()).get("jwt_token_expiry").toString()
                        token.postValue(jwtExpiry)
                    } catch (e: Exception) {
                        Log.e(TAG, "Wrong Json from POST gates")
                        message.postValue(server_error)
                    }
                    400 -> {
                        Log.e(TAG, "Invalid Json provided to the POST session token")
                        message.postValue(invalid_data)
                    }
                    401 -> {
                        //We reset both, so that we actually delete the memory of the wrong token
                        Log.i(TAG, "Invalid token provided in POST session token")
                        token.postValue("")
                        refreshToken.postValue("")
                    }

                }
            }

        })
    }

    fun login(email: String, password: String) {
        val credential = Credentials.basic(email, password)
        val request = Request.Builder()
                .url(URL + "user/login")
                .addHeader("Authorization", credential)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for GET login")
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> try {
                        val json = JSONObject(response.body!!.string())
                        val jwtRefresh = json.get("jwt_token").toString()
                        val jwtExpiry = json.get("jwt_token_expiry").toString()
                        refreshToken.postValue(jwtRefresh)
                        token.postValue(jwtExpiry)
                    } catch (e: Exception) {
                        Log.e(TAG, "Wrong Json from GET login")
                        message.postValue(server_error)
                    }
                    400 -> {
                        Log.e(TAG, "Invalid authorization provided to GET login")
                        message.postValue(invalid_data)
                    }
                    401 -> {
                        Log.e(TAG, "User not found in GET login")
                        message.postValue(invalid_user)
                    }
                }
            }
        })
    }

    fun signIn(email: String, password: String) {
        val body = """{"email":"$email", "password":"$password"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
            .url(URL + "user/signin")
            .post(requestBody)
            .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
                Log.e(TAG, "Failed contacting server for POST sign in")
                loading.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue(success)
                    409 -> {
                        Log.e(TAG, "User already exists in POST sign in")
                        message.postValue("User already exists!")
                    }
                    500 -> {
                        Log.e(TAG, "Server failed POST sign in")
                        message.postValue(server_error)
                    }
                }
                loading.postValue(false)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}