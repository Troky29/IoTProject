package com.example.iotproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.fragments.car.CarFragment
import com.example.iotproject.fragments.activity.ActivityFragment
import com.example.iotproject.fragments.gate.GateFragment
import com.example.iotproject.login.Login
import com.example.iotproject.services.FirebaseService
import com.example.iotproject.services.LocationService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

class MainActivity : AppCompatActivity(),UserFragmentDialog.LogoutListener{
    private val REQUEST_CODE = 1
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.message.observe(this, { message -> messenger(message) })

        //We load the saved information of the user, if we have just logged out we need to reload them
        val gson = Gson()
        val sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val userPref = sharedPreferences.getString("user", null)
        if (userPref != null) {
            val user: User = gson.fromJson(userPref, User::class.java)
            Log.e("ActivityMain", user.nickname)
            findViewById<TextView>(R.id.toolbarTextView).text = user.nickname
        } else {
            viewModel.getUser()
        }
        viewModel.user.observe(this, { user ->
            val editor = sharedPreferences.edit()
            editor.putString("user", gson.toJson(user)).apply()
        })

        val profilePicture = findViewById<ImageButton>(R.id.profileButton)
        profilePicture.setOnClickListener {
            val userFragment = UserFragmentDialog(this)
            userFragment.show(supportFragmentManager, "UserFragmentDialog")
        }

        val moreFragment = CarFragment()
        val gateFragment = GateFragment()
        val activityFragment = ActivityFragment()

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

    override fun logout() {
        //We remove the saved user info
        val prefEditor = getSharedPreferences("userPref", MODE_PRIVATE).edit()
        prefEditor.putString("user", null).apply()
        //Clear database to be reloaded upon new login
        (application as IoTApplication).clearAll()

        viewModel.logout()

        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
