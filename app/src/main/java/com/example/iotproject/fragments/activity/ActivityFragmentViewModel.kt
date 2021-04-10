package com.example.iotproject.fragments.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.AccessTokenAuthenticator
import com.example.iotproject.AccessTokenInterceptor
import com.example.iotproject.AccessTokenRepository
import com.example.iotproject.Constants
import com.example.iotproject.fragments.gate.Gate
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ActivityFragmentViewModel : ViewModel() {
    val TAG = "ActivityFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
        .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
        .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val activityList: MutableLiveData<List<Activity>> by lazy {
        MutableLiveData<List<Activity>>().also {
            loadActivities()
        }
    }

    private fun loadActivities() {
        val request = Request.Builder()
            .url(Constants.URL + "activity")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(Constants.server_error)
            }
            //TODO:correct with updated information
            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> try {
                        val json = JSONArray(response.body!!.string())
                        val list = ArrayList<Activity>()
                        for (index in 0 until json.length()) {
                            val item = json.getJSONObject(index)
                            val name = item.get("name").toString()
                            val id = item.get("id").toString()
                            list.add(Activity(id))
                        }
                        activityList.postValue(list)
                    } catch (e: Exception) {
                        message.postValue(Constants.server_error)
                    }
                    400 -> message.postValue(Constants.server_error)
                    404 -> message.postValue(Constants.no_gates)
                    //TODO: curate the responses, they are wrong as is
                }
            }
        })
    }
}