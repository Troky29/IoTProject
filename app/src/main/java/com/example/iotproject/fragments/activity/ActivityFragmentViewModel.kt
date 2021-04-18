package com.example.iotproject.fragments.activity

import android.util.Log
import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.Constants.Companion.JSON
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList

class ActivityFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    private val TAG = "ActivityFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
        .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
        .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val activityList: LiveData<List<Activity>> = repository.allActivities

    fun loadActivities() {
        val request = Request.Builder()
            .url(Constants.URL + "activity")
            .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for GET activities")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> try {
                        val json = JSONObject(response.body!!.string())
                        val activities = json.getJSONArray("activities")
                        val guestActivities = json.getJSONArray("guests_activities")
                        val list = ArrayList<Activity>()
                        for (index in 0 until activities.length()) {
                            val activity = getActivity(activities.getJSONObject(index))
                            list.add(activity)
                        }
                        for(index in 0 until guestActivities.length()) {
                            val guestActivity = getActivity(guestActivities.getJSONObject(index))
                            list.add(guestActivity)
                        }
                        deleteAll()
                        insertAll(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Wrong Json from GET actives")
                        message.postValue(Constants.server_error)
                    }
                    500 -> {
                        Log.e(TAG, "Server failed GET activities")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }

    fun setAction(position: Int, action: String) {
        val activity = activityList.value?.get(position)!!
        updateActivity(Activity(activity.id, activity.gate, activity.datetime, "Updating",
                activity.car, activity.imageURL))
        val body = """{"id_gate":"${activity.gate}", "outcome":"$action"}""".trimMargin()

        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(Constants.URL + "activity")
                .put(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for PUT updating activity")
                message.postValue(Constants.server_error)
                updateActivity(activity)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        message.postValue("Action received!")
                        updateActivity(Activity(activity.id, activity.gate, activity.datetime,
                                action, activity.car, activity.imageURL))
                    }
                    404 -> {
                        Log.e(TAG, "Error, no activity found")
                        message.postValue("Internal error, deleted activity")
                        deleteActivity(activity)
                    }
                    500 -> {
                        Log.e(TAG, "Server failed PUT update activity")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }

    private fun getActivity(item: JSONObject): Activity {
        val gate = item.get("ID_Gate").toString()
        val dateTime = item.get("Date_Time").toString()
        val state = item.get("Outcome").toString()
        val car = item.get("ID_Car").toString()
        val imageResource = item.get("Photo").toString()
        return Activity(0, gate, dateTime, state, car, imageResource)
    }

   private fun insertAll(activities: List<Activity>) = viewModelScope.launch {
        repository.insertAllActivities(activities)
    }

    private fun deleteActivity(activity: Activity) = viewModelScope.launch {
        repository.deleteActivity(activity)
    }

    private fun deleteAll() = viewModelScope.launch {
        repository.deleteAllActivities()
    }

    private fun updateActivity(activity: Activity) = viewModelScope.launch {
        repository.updateActivity(activity)
    }

    fun getGateName(code: String) = repository.allGates.value?.first { it.code == code }?.name
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