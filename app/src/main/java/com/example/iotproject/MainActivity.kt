package com.example.iotproject

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        // toolbar.setTitleTextColor(Color.white)
        val profilePicture = findViewById<ImageButton>(R.id.profile_button)
        profilePicture.setOnClickListener() {
            Toast.makeText(this, "Profile image", Toast.LENGTH_SHORT).show()
            //TODO: Put profile settings (maybe other View)
        }

        val navigation = findViewById<BottomNavigationView>(R.id.navigation_bar)
        val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.gate_page -> {
                        //TODO: go to selected page
                        Toast.makeText(this, "Gate page", Toast.LENGTH_SHORT)
                        true
                    }
                    R.id.activity_page -> {
                        //TODO: go to activity page
                        Toast.makeText(this, "Activity page", Toast.LENGTH_SHORT)
                        true
                    }
                    R.id.more_page -> {
                        //TODO: go to more page
                        Toast.makeText(this, "More page", Toast.LENGTH_SHORT)
                        true
                    }
                    else -> false
                }
            }

        navigation.setOnNavigationItemSelectedListener(navigationListener)

    }

}