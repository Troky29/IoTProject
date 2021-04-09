package com.example.iotproject.fragments.gate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iotproject.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException

class GateFragmentViewModel : ViewModel() {
    val TAG = "GateFragmentViewModel"

    private var client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .build()

    val message: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val gateList: MutableLiveData<List<Gate>> by lazy {
        MutableLiveData<List<Gate>>().also {
            loadGates()
        }
    }

    fun loadGates() {
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
                        val list = ArrayList<Gate>()
                        for (index in 0 until json.length()) {
                            val item = json.getJSONObject(index)
                            val name = item.get("name").toString()
                            val location = item.get("location").toString()
                            val code = item.get("code").toString()
                            list.add(Gate(name, location, code))
                        }
                        gateList.postValue(list)
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
                    200 -> message.postValue("Successfully added gate!")
                    400 -> message.postValue(Constants.invalid_data)
                    409 -> message.postValue("Gate already exists!")
                    500 -> message.postValue(Constants.server_error)
                }
            }
        })
    }
}