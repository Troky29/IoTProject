package com.example.iotproject

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.Constants.Companion.notification_content
import com.example.iotproject.Constants.Companion.notification_title
import com.example.iotproject.fragments.activity.ActivityFragment
import com.example.iotproject.fragments.gate.GateFragment
import com.example.iotproject.fragments.MoreFragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

const val TOPIC = "myTopic"

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 1
    private val CHANNEL_ID = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val messageObserver = Observer<String> { message -> messenger(message) }
        viewModel.message.observe(this, messageObserver)

        val moreFragment = MoreFragment()
        val gateFragment = GateFragment()
        val activityFragment = ActivityFragment()

        val profilePicture = findViewById<ImageButton>(R.id.profileButton)
        profilePicture.setOnClickListener() {
            Toast.makeText(this, "Profile image", Toast.LENGTH_SHORT).show()
            //TODO: Put profile settings (maybe other activity)
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.gate_page -> {
                    replaceFragment(gateFragment)
                    true
                }
                R.id.activity_page -> {
                    replaceFragment(activityFragment)
                    //TODO: just for testing, remember to delete
                    //updateGPS()
                    true
                }
                R.id.more_page -> {
                    replaceFragment(moreFragment)
                    true
                }
                else -> false
            }
        }

        viewModel.getGates().observe(this, Observer { gates ->
            gateFragment.flushGateCards()
            for (gate in gates)
                gateFragment.addGateCard(gate.name, gate.location, gate.state, gate.id)
            replaceFragment(gateFragment)
        })

        viewModel.getActivities().observe(this, Observer { activities ->
            //TODO: Update UI with list of activities
        })

        val intent = Intent(this, LocationService::class.java)
        startService(intent)

        //createNotificationChannel()
        //Testing push notification routine
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        FirebaseService.sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseService.token = token
            //TODO: we have to save on server side this
        }
        /*
        PushNotification(NotificationData("title", "content"), TOPIC).also { notification ->
            sendNotification(notification)
        }

        val title = "Title"
        val message = "Message"
        val token = FirebaseService.token!!
        if(title.isNotEmpty() && message.isNotEmpty() && token.isNotEmpty()) {
            PushNotification(NotificationData(title, message), token).also { notification ->
            sendNotification(notification)
        }

         */
        createNotificationChannel()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d("MainActivity", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("MainActivity", response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e("MainActivity", e.toString())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Access alert"
            val descriptionText = "This channel is used to notify the user for any activity detected"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //TODO: These are test for the creation of the notification, place them in the right position
        //This triggers a call when there is a change in the airplane mode
        val abr: BroadcastReceiver = ActivityBroadcastReceiver()
        val filter = IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(abr, filter)

        val intent = Intent(this, ActivityDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val openIntent = Intent(this, ActivityBroadcastReceiver::class.java).apply {
            action = "OPEN"
            putExtra("EXTRA_NOTIFICATION_ID", 0)
        }
        val openPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, openIntent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_gate_access)
            .setContentTitle(notification_title)
            .setContentText(notification_content)
            //.setStyle(NotificationCompat.BigTextStyle().bigText(notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_gate_access, "OPEN", openPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        //TODO: removed for testing
        //startLocationUpdates()
    }

    //TODO: change this to make sure that you have the correct permission for sending the user location
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE)
            return
        }


        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            //maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //TODO: you should call the unregisterReceiver method on the BroadcastReceiver you created
    }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}