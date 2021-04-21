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
                            val user = json.getJSONObject("user")
                            val nickname = user.get("Nickname").toString()
                            val email = user.get("Email").toString()
                            val photo = user.get("Photo").toString()
                            insert(User(nickname, email, photo))
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
        //TODO: upon opening the user icon dialog you can see all the info and logout
    }

    private fun insert(user: User) {
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}

data class User(val nickname: String, val email: String, val photo: String?)