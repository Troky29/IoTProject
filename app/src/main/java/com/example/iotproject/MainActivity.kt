package com.example.iotproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.Constants.Companion.TOPIC
import com.example.iotproject.fragments.more.MoreFragment
import com.example.iotproject.fragments.activity.ActivityFragment
import com.example.iotproject.fragments.gate.GateFragment
import com.example.iotproject.services.FirebaseService
import com.example.iotproject.services.LocationService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 1

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
            if (gates.isNotEmpty())
                gateFragment.hideEmpty()
            for (gate in gates)
                gateFragment.addGateCard(gate.name, gate.location, gate.state, gate.id)
            replaceFragment(gateFragment)
        })

        viewModel.getActivities().observe(this, Observer { activities ->
            //TODO: Update UI with list of activities
        })

        //We check for the permission for obtaining the location and start the service
        checkPermissions()
        val intent = Intent(this, LocationService::class.java)
        //TODO: Insert back location updates
        //startService(intent)

        //This code retrieves the token that unequivocally identifies the device
        checkGooglePlayServices()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        FirebaseService.sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (FirebaseService.token != token) {
                FirebaseService.token = token
                //TODO: we have to save on server side this
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkGooglePlayServices()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    private fun checkGooglePlayServices() {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
            return
        }
    }

    private fun checkPermissions() {
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
    }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}