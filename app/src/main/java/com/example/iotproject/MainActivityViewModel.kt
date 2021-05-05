package com.example.iotproject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.iotproject.database.AppRepository
import com.example.iotproject.fragments.gate.GateFragmentViewModel
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class MainActivityViewModel(private val repository: AppRepository) : ViewModel() {
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

    fun updateFCMToken(token: String) {
        val body = """{"fcm_token":"$token"}"""
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "fcm")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed contacting server for POST update FCM")
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> Log.i(TAG, "Correctly updated FCM token")
                    400 -> Log.e(TAG, "Invalid input data POST update FCM")
                    500 -> Log.e (TAG, "Server failed POST update FCM")
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
                    200 -> {
                        message.postValue(Constants.success)
                        //We clear the database upon logout
                        clearAll()
                    }
                    404 -> Log.e(TAG, "User not found in GET logout")
                    500 -> Log.e(TAG, "Server failed GET logout")
                }
            }
        })
        AccessTokenRepository.logout = true
    }

    private fun clearAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class User(val nickname: String, val email: String, val photo: String?)