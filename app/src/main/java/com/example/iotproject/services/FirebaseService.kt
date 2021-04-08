package com.example.iotproject.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.iotproject.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
import com.example.iotproject.Constants.Companion.State
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class FirebaseService : FirebaseMessagingService() {
    val TAG = "FirebaseService"
    private val CHANNEL_ID = "my_channel"

    companion object{
        var sharedPreferences: SharedPreferences? = null

        var token: String?
        get() { return sharedPreferences?.getString("fbToken", "") }
        set(value) { sharedPreferences?.edit()?.putString("fbToken", value)?.apply() }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        updateFCMToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)

        val allowBroadcastIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("requestCode", State.ALLOW)
            putExtra("notificationID", notificationID)
        }
        val allowAction = PendingIntent.getBroadcast(this, 0, allowBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val denyBroadcastIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("requestCode", State.DENY)
            putExtra("notificationID", notificationID)
        }
        val denyAction = PendingIntent.getBroadcast(this, 0, denyBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val reportBroadcastReceiver = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("requestCode", Constants.Companion.State.REPORT)
            putExtra("notificationID", notificationID)
        }
        val reportAction = PendingIntent.getBroadcast(this, 0, reportBroadcastReceiver, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.notification!!.title)
            .setContentText(remoteMessage.notification!!.body)
            .setSmallIcon(R.drawable.ic_gate_access)
            .setColor(resources.getColor(R.color.ColorPrimary, theme))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_baseline_check_24, "ALLOW", allowAction)
            .addAction(R.mipmap.ic_launcher, "DENY", denyAction)
            .addAction(R.mipmap.ic_launcher, "REPORT", reportAction)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Access alert"
            val descriptionText = "This channel is used to notify the user for any activity detected"
            val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_HIGH).apply {
                description = descriptionText
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateFCMToken(token: String) {
        val client: OkHttpClient = OkHttpClient().newBuilder()
                .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
                .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
                .build()

        val body = """{"fcm_token":"$token"}"""
        val requestBody = body.toRequestBody(Constants.JSON)

        val request = Request.Builder()
                .url(Constants.URL + "activity")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                when (response.code) {
                    200 -> Log.i(TAG, "Correctly updated FCM token")
                    //TODO: add all cases
                    500 -> Log.e (TAG, Constants.server_error)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, Constants.destroyed)
    }
}