package com.example.iotproject.fragments.activity

import android.util.Log
import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import com.example.iotproject.Constants.Companion.server_error
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ActivityFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    val TAG = "ActivityViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
        .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
        .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val activityList: LiveData<List<Activity>> = repository.allActivities

    fun loadActivities() {
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
                        val json = JSONObject(response.body!!.string())
                        val activities = json.getJSONArray("activities")
                        val guestActivities = json.getJSONArray("guest_activities")
                        //val json = JSONArray(response.body!!.string())
                        //TODO: if it works remember to include also guest activities
                        val list = ArrayList<Activity>()
                        for (index in 0 until activities.length()) {
                            val item = activities.getJSONObject(index)
                            val gate = item.get("ID_Gate").toString()
                            val dateTime = item.get("Date_Time").toString()
                            val state = item.get("Outcome").toString()
                            val imageResource = item.get("Photo").toString()
                            list.add(Activity(gate, dateTime, state, imageResource))
                        }
                        insertAll(list)
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

    fun updateActivity(position: Int, action: String) {
        val gate = activityList.value?.get(position)!!.gate
        val body = """{"id_gate":"$gate", "outcome":"$action"}""".trimMargin()

        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "activity")
                .put(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue("Successfully updated gate")
                    404 -> message.postValue("Error, no activity found")
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }

    //TODO: keep private
   fun insertAll(activities: List<Activity>) = viewModelScope.launch {
        repository.insertAllActivities(activities)
    }

    //TODO: keep private
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllActivities()
    }
}

class ActivityViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}