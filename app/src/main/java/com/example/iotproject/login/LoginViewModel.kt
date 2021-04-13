package com.example.iotproject.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.AccessTokenAuthenticator
import com.example.iotproject.AccessTokenInterceptor
import com.example.iotproject.AccessTokenRepository
import com.example.iotproject.Constants
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.invalid_data
import com.example.iotproject.Constants.Companion.invalid_token
import com.example.iotproject.Constants.Companion.invalid_user
import com.example.iotproject.Constants.Companion.not_found
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
                message.postValue(server_error)
                loading.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> try {
                        val jwtExpiry = JSONObject(response.body!!.string()).get("jwt_token_expiry").toString()
                        token.postValue(jwtExpiry)
                    } catch (e: Exception) {
                        message.postValue(server_error)
                    }
                    400 -> message.postValue(invalid_data)
                    401 -> {    //We reset both, so that we actually delete the memory of the wrong token
                        Log.i(TAG, invalid_token)
                        token.postValue("")
                        refreshToken.postValue("")
                    }

                }
                loading.postValue(false)
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
                message.postValue(server_error)
                loading.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> try {
                        val json = JSONObject(response.body!!.string())
                        val jwtRefresh = json.get("jwt_token").toString()
                        val jwtExpiry = json.get("jwt_token_expiry").toString()
                        refreshToken.postValue(jwtRefresh)
                        token.postValue(jwtExpiry)
                    } catch (e: Exception) {
                        message.postValue(server_error)
                    }
                    400 -> message.postValue(invalid_data)
                    401 -> message.postValue(invalid_user)
                }
                loading.postValue(false)
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
                loading.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue(success)
                    409 -> message.postValue("User already exists!")
                    500 -> message.postValue(server_error)
                }
                loading.postValue(false)
            }
        })
    }

    fun logout() {
        val logoutClient: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

        val request = Request.Builder()
            .url(URL + "user/logout")
            .build()

        logoutClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> Log.i(TAG, success)
                    401 -> Log.i(TAG, invalid_token)
                    404 -> Log.i(TAG, not_found)
                    500 -> message.postValue(server_error)
                }
            }
        })
        AccessTokenRepository.logout = true
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}