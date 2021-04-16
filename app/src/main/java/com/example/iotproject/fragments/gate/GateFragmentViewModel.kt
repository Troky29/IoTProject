package com.example.iotproject.fragments.gate

import android.util.Log
import androidx.lifecycle.*
import com.example.iotproject.*
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.URL
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
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
        Log.i(TAG, "Updating gates")
        val request = Request.Builder()
                .url(Constants.URL + "gate")
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading.postValue(false)
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
                            //TODO: check if useful, we can also get latitude and longitude if needed
                            //val idUser = item.get("ID_User")
                            val name = item.get("Name").toString()
                            val location = item.get("Location").toString()
                            val code = item.get("ID").toString()
                            val imageURL = item.get("Photo").toString()
                            list.add(Gate(name, location, code, imageURL))
                        }
                        insertAll(list)
                    } catch (e: Exception) {
                        message.postValue(Constants.server_error)
                    }
                    404 -> message.postValue(Constants.no_gates)
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }

    fun addGate(name: String, location: String, latitude: Double, longitude: Double, code: String, image: String?) {
        val body = """{"name":"$name", "location":"$location", "latitude":"$latitude", 
            |"longitude":"$longitude", "id_gate":"$code", "photo":"$image"}""".trimMargin()
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "gate")
                .post(requestBody)
                .build()

        loading.postValue(true)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //TODO: delete this
                insert(Gate(name, location, code, null))
                loading.postValue(false)
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                loading.postValue(false)
                when (response.code) {
                    200 -> {
                        message.postValue("Successfully added gate!")
                        val json = JSONObject(response.body!!.string())
                        //TODO: after checking the functionality correct
                        try {
                            val imageUrl = json.getString("url_image")
                            Log.i(TAG, "Success: $imageUrl")
                        } catch (e: Exception) {
                            Log.i(TAG, "è crashato")
                        }
                        insert(Gate(name, location, code, null))
                    }
                    400 -> message.postValue(Constants.invalid_data)
                    409 -> message.postValue("Gate already exists!")
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }

    //TODO: update with call to the corresponding endpoint
    fun openGate(position: Int) {
        val gate = gateList.value?.get(position)?.code
        val body = """{"id_gate":"$gate"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "gate/open")
                .post(requestBody)
                .build()

        //TODO: add loading
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message.postValue(Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> message.postValue("Success!")
                    400 -> message.postValue("Error with the gate")
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