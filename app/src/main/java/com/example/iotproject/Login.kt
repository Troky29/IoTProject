package com.example.iotproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

const val EMAIL = "com.example.iotproject.EMAIL"
const val TOKEN = "com.example.iotproject.TOKEN"

class Login : AppCompatActivity() {
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tokenObserver = Observer<String> { token -> if (token.isNotEmpty()) login(token) }
        val messageObserver = Observer<String> { message -> messenger(message) }

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.token.observe(this, tokenObserver)
        loginViewModel.message.observe(this, messageObserver)

        if (loginViewModel.alreadyLoggedIn()) {
            loginViewModel.getSessionToken()
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener() {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun login(sessionToken: String) {
        //messenger(sessionToken)
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL, email)
            putExtra(TOKEN, sessionToken)
        }
        startActivity(intent)
    }

    fun logout(){
        //TODO: delete refresh token from secure shared preferences
    }

    private fun signin() {
        //TODO: add possibility for a user to sign in to the service
    }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}