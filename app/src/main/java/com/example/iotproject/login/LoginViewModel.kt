package com.example.iotproject.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.AccessTokenRepository
import com.example.iotproject.Constants
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.invalid_data
import com.example.iotproject.Constants.Companion.invalid_user
import com.example.iotproject.Constants.Companion.server_error
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginViewModel : ViewModel() {
    val TAG = "LoginViewModel"
    private val client = OkHttpClient()
    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val token: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val refreshToken: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getSessionToken(jwtRefresh: String)  {

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jwt_refresh", jwtRefresh)
                .build()

        val request = Request.Builder()
                .url(URL + "jwt")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
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
                    401 -> { /*TODO: token is incorrect, that means that you have to login*/ }
                }
            }
        })
    }

    fun login(email: String, password: String) {
        val credential = Credentials.basic(email, password)
        val request = Request.Builder()
                .url(URL + "login")
                .addHeader("Authorization", credential)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(server_error)
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
            }
        })
    }

    fun logout() {
        AccessTokenRepository.logout = true
        AccessTokenRepository.token = ""
        AccessTokenRepository.refreshToken = ""
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}