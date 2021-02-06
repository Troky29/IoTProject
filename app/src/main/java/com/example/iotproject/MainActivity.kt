package com.example.iotproject

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val email = intent.getStringExtra(EMAIL)
        findViewById<TextView>(R.id.toolbarTextView).text = email

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
                    replaceFragment(GateFragment())
                    Toast.makeText(this, "Gate page", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.activity_page -> {
                    //TODO: change to activity frame
                    replaceFragment(ActivityFragment())
                    Toast.makeText(this, "Activity page", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.more_page -> {
                    //TODO: change to more frame
                    replaceFragment(MoreFragment())
                    Toast.makeText(this, "More page", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        replaceFragment(GateFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }

}