package com.example.iotproject.fragments.gate

import android.util.Log
import androidx.lifecycle.*
import com.example.iotproject.*
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException

class GateFragmentViewModel(private val repository: GateRepository) : ViewModel() {
    val TAG = "GateFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val gateList: LiveData<List<Gate>> = repository.allGates

    fun loadGates() {
        Log.i(TAG, "Updating gates")
        val request = Request.Builder()
                .url(Constants.URL + "gate")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> try {
                        val json = JSONArray(response.body!!.string())
                        val list = mutableListOf<Gate>()
                        for (index in 0 until json.length()) {
                            val item = json.getJSONObject(index)
                            val name = item.get("name").toString()
                            val location = item.get("location").toString()
                            val code = item.get("code").toString()
                            list.add(Gate(name, location, code, null))
                            //insert(Gate(name, location, code, null))
                        }
                        //TODO: see if this actually works
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

    fun addGate(name: String, location: String, latitude: Double, longitude: Double, code: String) {
        val body = """{"name":"$name", "location":"$location", "latitude":"$latitude", 
            |"longitude", "$longitude" "id_gate":"$code"}""".trimMargin()
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "gate")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added gate!")
                        insert(Gate(name, location, code, null))
                    }
                    400 -> message.postValue(Constants.invalid_data)
                    409 -> message.postValue("Gate already exists!")
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }

    private fun insert(gate: Gate) = viewModelScope.launch {
        repository.insert(gate)
    }

    private fun insertAll(gates: List<Gate>) = viewModelScope.launch {
        repository.insertAll(gates)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}

class GateViewModelFactory(private val repository: GateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GateFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GateFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}