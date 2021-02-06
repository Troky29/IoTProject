package com.example.iotproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //TODO: check in the preferences if i am already logged, otherwise procede as normal

        findViewById<Button>(R.id.loginButton).setOnClickListener() {
            //TODO: lauch function to check credential, if ok go further
            login(it)
        }
    }

    private fun login(view: View) {
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}