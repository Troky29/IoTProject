package com.example.iotproject

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
                "encrypted_preferences",
                masterKeyAlias,
                getApplication(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    val encrypted: SharedPreferences.Editor = sharedPreferences.edit()
    private val client = OkHttpClient()
    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val token: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun alreadyLoggedIn() = sharedPreferences.contains("jwtRefresh") &&
                sharedPreferences.getString("jwtRefresh", "")!!.isNotEmpty()

    fun getSessionToken()  {
        val jwtRefresh = sharedPreferences.getString("jwtRefresh", "")

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jwt_refresh", jwtRefresh!!)
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
                    403 -> message.postValue(invalid_data)
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
                        encrypted.putString("jwtRefresh", jwtRefresh).apply()
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

    fun logout() { encrypted.putString("jwtRefresh", null).apply() }

    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "Login destroyed")
    }
}