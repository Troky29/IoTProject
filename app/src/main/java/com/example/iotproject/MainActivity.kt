package com.example.iotproject

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val email = intent.getStringExtra(EMAIL)
        findViewById<TextView>(R.id.toolbarTextView).text = email

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val moreFragment = MoreFragment()
        val gateFragment = GateFragment()
        val activityFragment = ActivityFragment()

        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        // toolbar.setTitleTextColor(Color.white)

        val profilePicture = findViewById<ImageButton>(R.id.profileButton)
        profilePicture.setOnClickListener() {
            Toast.makeText(this, "Profile image", Toast.LENGTH_SHORT).show()
            //TODO: Put profile settings (maybe other activity)
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.gate_page -> {
                    //TODO: change to gate frame
                    replaceFragment(gateFragment)
                    Toast.makeText(this, "Gate page", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.activity_page -> {
                    //TODO: change to activity frame
                    replaceFragment(activityFragment)
                    Toast.makeText(this, "Activity page", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.more_page -> {
                    //TODO: change to more frame
                    replaceFragment(moreFragment)
                    Toast.makeText(this, "More page", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        replaceFragment(gateFragment)
        getLocation()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

    fun getLocation() {
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                findViewById<TextView>(R.id.toolbarTextView).text = location.latitude.toString()
            }
        }
    }
}