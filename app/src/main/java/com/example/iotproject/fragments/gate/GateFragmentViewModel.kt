package com.example.iotproject.fragments.gate

import android.util.Log
import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.database.AppRepository
import com.example.iotproject.database.Gate
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class GateFragmentViewModel(private val repository: AppRepository) : ViewModel() {
    val TAG = "GateFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val gateList: LiveData<List<Gate>> = repository.allGates

    fun loadGates() {
        val request = Request.Builder()
                .url(Constants.URL + "gate")
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for GET gates")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> try {
                        val json = JSONObject(response.body!!.string())
                        val gates = json.getJSONArray("gates")
                        val list = mutableListOf<Gate>()
                        for (index in 0 until gates.length()) {
                            val item = gates.getJSONObject(index)
                            val code = item.get("ID").toString()
                            val name = item.get("Name").toString()
                            val location = item.get("Location").toString()
                            val imageURL = item.get("Photo").toString()
                            list.add(Gate(code, name, location, imageURL))
                        }
                        insertAll(list)
                    } catch (e: Exception) {
                        Log.e(TAG, "Wrong Json from GET gates")
                        message.postValue(Constants.server_error)
                    }
                    404 -> {
                        Log.i(TAG, "No gates found, deleting all local gates")
                        deleteAll()
                    }
                    500 -> {
                        Log.e(TAG, "Server failed GET gates")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }

    fun addGate(name: String, location: String, latitude: Double, longitude: Double, code: String, image: String?) {
        insert(Gate(code, name, location, null))
        return
        val body = """{"name":"$name", "location":"$location", "latitude":"$latitude", 
            |"longitude":"$longitude", "id_gate":"$code", "photo":"$image"}""".trimMargin()
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "gate")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting server for POST gate")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added gate!")
                        val json = JSONObject(response.body!!.string())
                        //The server returns a Json with the url of the saved image only if its uploaded
                        val imageURL: String? = try {
                            json.getString("url_image")
                        } catch (e: Exception) { null }

                        insert(Gate(code, name, location, imageURL))
                    }
                    400 -> {
                        Log.e(TAG, "Invalid data provided to POST gate")
                        message.postValue(Constants.invalid_data)
                    }
                    409 -> {
                        Log.e(TAG, "The gate in the POST request is already registered")
                        message.postValue("Gate already exists!")
                    }
                    500 -> {
                        Log.e(TAG, "Server failed POST gate")
                        message.postValue(Constants.server_error)
                    }
                }
            }
        })
    }


    fun openGate(position: Int) {
        val gate = gateList.value?.get(position)
        val code = gate?.code
        val body = """{"id_gate":"$code"}"""
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "gate/open")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
                Log.e(TAG, "Failed contacting the server for POST open gate")
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> message.postValue("Gate open!")
                    400 -> {
                        Log.e(TAG, "Error in POST, no gate found")
                        message.postValue("Internal error, deleted gate")
                        delete(gate!!)
                    }
                }
            }
        })
    }

    private fun insert(gate: Gate) = viewModelScope.launch {
        repository.insertGate(gate)
    }

    private fun insertAll(gates: List<Gate>) = viewModelScope.launch {
        repository.insertAllGates(gates)
    }

    private fun delete(gate: Gate) = viewModelScope.launch {
        repository.deleteGate(gate)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllGates()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, Constants.destroyed)
    }
}

class GateViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GateFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GateFragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}