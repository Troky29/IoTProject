package com.example.iotproject.fragments.activity

import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.fragments.gate.Gate
import com.example.iotproject.fragments.gate.GateFragmentViewModel
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ActivityFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    val TAG = "ActivityFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
        .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
        .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val activityList: LiveData<List<Activity>> = repository.allActivities

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
                            val gate = item.get("gate").toString()
                            val dateTime = item.get("datetime").toString()
                            val state = item.get("state").toString()
                            list.add(Activity(gate, dateTime, state, null))
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

    private fun insertAll(activities: ArrayList<Activity>) = viewModelScope.launch {
        repository.insertAllActivities(activities)
    }

    private fun deleteAll() = viewModelScope.launch {
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