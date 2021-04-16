package com.example.iotproject.fragments.activity

import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList

class ActivityFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    val TAG = "ActivityViewModel"

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
                message.postValue(Constants.server_error)
            }
            //TODO:correct with updated information
            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> try {
                        val json = JSONObject(response.body!!.string())
                        val activities = json.getJSONArray("activities")
                        val guestActivities = json.getJSONArray("guests_activities")
                        //TODO: if it works remember to include also guest activities
                        val list = ArrayList<Activity>()
                        for (index in 0 until activities.length()) {
                            val item = activities.getJSONObject(index)
                            val gate = item.get("ID_Gate").toString()
                            val dateTime = item.get("Date_Time").toString()
                            val state = item.get("Outcome").toString()
                            val imageResource = item.get("Photo").toString()
                            list.add(Activity(0, gate, dateTime, state, imageResource))
                        }
                        deleteAll()
                        insertAll(list)
                    } catch (e: Exception) {
                        message.postValue(Constants.server_error)
                    }
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }

    fun setAction (position: Int, action: String) {
        val activity = activityList.value?.get(position)!!
        updateActivity(Activity(activity.id, activity.gate, activity.datetime, "Updating", activity.imageURL))
        val body = """{"id_gate":"${activity.gate}", "outcome":"$action"}""".trimMargin()

        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "activity")
                .put(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                message.postValue(Constants.server_error)
                updateActivity(activity)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully updated gate")
                        updateActivity(Activity(activity.id, activity.gate, activity.datetime, action, activity.imageURL))
                    }
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

    fun updateActivity(activity: Activity) = viewModelScope.launch {
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