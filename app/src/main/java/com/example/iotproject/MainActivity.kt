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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        // toolbar.setTitleTextColor(Color.white)
        val profilePicture = findViewById<ImageButton>(R.id.profile_button)
        profilePicture.setOnClickListener(){
            Toast.makeText(this, "Profile image", Toast.LENGTH_SHORT).show()
            //TODO: Put profile settings (maybe other View)

        }
    }



}