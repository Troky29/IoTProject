package com.example.iotproject.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.iotproject.*
import com.example.iotproject.Constants.Companion.EMAIL

class Login : AppCompatActivity() {
    private val loadingDialog by lazy { LoadingDialog() }

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
        //If we have just logged out we need to delete the stored encrypted access token
        if (AccessTokenRepository.logout)
            logout(encrypted)
        val refreshToken = sharedPreferences.getString("jwtRefresh", "")!!

        val tokenObserver = Observer<String> { token ->
            if (token.isNotEmpty())
                login(token, refreshToken)
        }
        val refreshTokenObserver = Observer<String> { token -> encrypted.putString("jwtRefresh", token).apply() }
        val messageObserver = Observer<String> { message -> messenger(message) }
        val loadingObserver = Observer<Boolean> { loading ->
            if (loading)
                loadingDialog.show(supportFragmentManager, "LoadingDialog")
            else
                loadingDialog.dismiss()
        }

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.token.observe(this, tokenObserver)
        loginViewModel.refreshToken.observe(this, refreshTokenObserver)
        loginViewModel.message.observe(this, messageObserver)
        loginViewModel.loading.observe(this, loadingObserver)

        //If we haven't logged out we can ask for a new access token without authentication
        if (refreshToken.isNotEmpty()) {
            loginViewModel.getSessionToken(refreshToken)
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener() {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            if (email.isEmpty()) {
                messenger("Insert an email address")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                messenger("Not a valid email address")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                messenger("Insert a password")
                return@setOnClickListener
            }
            loginViewModel.login(email, password)
        }

        findViewById<TextView>(R.id.signInTextView).setOnClickListener() {
            signIn()
        }
        //TODO: we skip the login process
        login("", "")
    }

    private fun login(sessionToken: String, refreshToken: String) {
        AccessTokenRepository.token = sessionToken
        AccessTokenRepository.refreshToken = refreshToken
        AccessTokenRepository.logout = false

        val email = findViewById<EditText>(R.id.emailEditText).text.toString()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL, email)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //TODO: see if its actually useful to send information from the Login View
        }
        (application as IoTApplication).repository.allGates
        startActivity(intent)
    }

    private fun signIn() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
    }

    private fun logout(encrypted: SharedPreferences.Editor) {
        encrypted.putString("jwtRefresh", "").apply()
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}