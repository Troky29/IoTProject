package com.example.iotproject

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import com.example.iotproject.Constants.Companion.JSON
import com.example.iotproject.Constants.Companion.State
import com.example.iotproject.Constants.Companion.URL
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class NotificationReceiver : BroadcastReceiver() {
    val TAG = "NotificationReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val notificationManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(intent.getIntExtra("notificationID", 0))

            when (intent.getSerializableExtra("requestCode")) {
                State.ALLOW -> { updateState("Allow") }
                State.DENY -> { updateState("Deny") }
                State.REPORT -> { updateState("Report") }
                else -> { Log.e(TAG, "No admissible action")}
            }
        }
    }

    private fun updateState(state: String) {
        val client: OkHttpClient = OkHttpClient().newBuilder()
                .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
                .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
                .build()

        val body = """{"state":"$state"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(URL + "activity")
                .put(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed contacting server for PUT update activity")
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> Log.i(TAG, "Correctly PUT updated activity")
                    400 -> Log.e(TAG, "Error in PUT update activity, invalid input data")
                    404 -> Log.e(TAG, "Error in PUT update activity, no activity found")
                    500 -> Log.e (TAG, "Server failed PUT update activity")
                }
            }
        })
    }
}