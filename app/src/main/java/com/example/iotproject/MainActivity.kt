package com.example.iotproject

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val viewModel: MainActivityViewModel by viewModels {
            MainActivityViewModelFactory(AccessTokenRepository)
        }
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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        /*
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

         */
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    //TODO: implement correct routine for sending the location information
                    viewModel.updateLocation(location)

                }
            }
        }
        //replaceFragment(gateFragment)
        //updateGPS()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

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
        }!!
    }

/*
    private fun updateGPS() {
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            val viewModel: MainActivityViewModel by viewModels {
                MainActivityViewModelFactory(AccessTokenRepository)
            }
            viewModel.updateLocation(location)
        }
    }

 */

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}