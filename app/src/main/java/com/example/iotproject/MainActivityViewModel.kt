package com.example.iotproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class MainActivityViewModel : ViewModel() {
    val TAG = "MainActivityViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val user: MutableLiveData<User> by lazy { MutableLiveData<User>() }

    fun getUser() {
        val request = Request.Builder()
                .url(Constants.URL + "user")
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting the server for GET user")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        try {
                            val json = JSONObject(response.body!!.string())
                            val info = json.getJSONObject("user")
                            val nickname = info.get("Nickname").toString()
                            val email = info.get("Email").toString()
                            val photo = info.get("Photo").toString()
                            user.postValue(User(nickname, email, photo))
                        } catch (E: Exception) {
                            Log.e(TAG, "Wrong Json from GET user")
                            message.postValue(Constants.server_error)
                        }
                    }
                    409 -> {
                        Log.e(TAG, "User not found in GET user")
                        message.postValue("User doesn't exists")
                    }
                    500 -> {
                        Log.e(TAG, "Server failed GET user")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }

    fun logout() {
        val request = Request.Builder()
            .url(Constants.URL + "user/logout")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed contacting the server for GET logout")
            }

            override fun onResponse(call: Call, response: Response) {
                when(response.code) {
                    200 -> Log.i(TAG, "Success GET logout")
                    404 -> Log.e(TAG, "User not found in GET logout")
                    500 -> Log.e(TAG, "Server failed GET logout")
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

data class User(val nickname: String, val email: String, val photo: String?)