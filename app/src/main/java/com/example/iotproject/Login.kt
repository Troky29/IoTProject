package com.example.iotproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences: SharedPreferences by lazy {
            EncryptedSharedPreferences.create(
                    "encrypted_preferences",
                    masterKeyAlias,
                   this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        val encrypted: SharedPreferences.Editor = sharedPreferences.edit()
        val refreshToken = if (AccessTokenRepository.logout) "" else
            sharedPreferences.getString("jwtRefresh", "")!!


        val tokenObserver = Observer<String> { token -> if (token.isNotEmpty()) login(token, refreshToken) }
        val refreshTokenObserver = Observer<String> { token -> encrypted.putString("jwtRefresh", token).apply() }
        val messageObserver = Observer<String> { message -> messenger(message) }

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.token.observe(this, tokenObserver)
        loginViewModel.refreshToken.observe(this, refreshTokenObserver)
        loginViewModel.message.observe(this, messageObserver)

        if (refreshToken.isNotEmpty()) { loginViewModel.getSessionToken(refreshToken) }

        findViewById<Button>(R.id.loginButton).setOnClickListener() {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun login(sessionToken: String, refreshToken: String) {
        AccessTokenRepository.token = sessionToken
        AccessTokenRepository.refreshToken = refreshToken
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL, email)
            //TODO: see if its actually usefull to send information from the Login View
        }
        startActivity(intent)
    }
/*
    fun logout(){
        //TODO: delete refresh token from secure shared preferences
    }
*/
    private fun signin() {
        //TODO: add possibility for a user to sign in to the service
    }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}