package com.example.iotproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.Constants.Companion.EMAIL
import com.example.iotproject.fragments.more.MoreFragment
import com.example.iotproject.fragments.activity.ActivityFragment
import com.example.iotproject.fragments.activity.ActivityFragmentViewModel
import com.example.iotproject.fragments.activity.ActivityViewModelFactory
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
        viewModel.message.observe(this, { message -> messenger(message) })

        val moreFragment = MoreFragment()
        val gateFragment = GateFragment()
        val activityFragment = ActivityFragment()

        findViewById<TextView>(R.id.toolbarTextView).text = intent.getStringExtra(EMAIL)
        val profilePicture = findViewById<ImageButton>(R.id.profileButton)

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
        bottomNavigation.selectedItemId = R.id.gate_page
        //We check for the permission for obtaining the location and start the service
        checkPermissions()
        val locationService = Intent(this, LocationService::class.java)
        startService(locationService)
        //We need this authorization for implementing the notification inside the application
        checkGooglePlayServices()
        //This code updates the token that unequivocally identifies the device
        FirebaseService.sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (FirebaseService.token != token) {
                FirebaseService.token = token
            }
        }

        //TODO: complete the initial call here, also load the user info and add them
        val activityViewModel: ActivityFragmentViewModel by viewModels {
            ActivityViewModelFactory((application as IoTApplication).repository)
        }
        activityViewModel.loadActivities()
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

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}