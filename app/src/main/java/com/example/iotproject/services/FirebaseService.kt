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
import com.example.iotproject.Constants
import com.example.iotproject.MainActivity
import com.example.iotproject.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

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
        //TODO: send this token to the server, to be saved along the user
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //TODO: in this we have to manage whenever we obtain
        Log.i(TAG, "message data: ${remoteMessage.data}")
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        //TODO: investigate if this is really necessary to us, used for clearing activities stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.notification!!.title)
            .setContentText(remoteMessage.notification!!.body)
            .setSmallIcon(R.drawable.ic_gate_access)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Access alert"
            val descriptionText = "This channel is used to notify the user for any activity detected"
            val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_HIGH).apply {
                description = descriptionText
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, Constants.destroyed)
    }
}