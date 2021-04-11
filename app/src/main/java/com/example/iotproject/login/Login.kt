package com.example.iotproject.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
        if (AccessTokenRepository.logout) logout(encrypted)
        val refreshToken = sharedPreferences.getString("jwtRefresh", "")!!

        val tokenObserver = Observer<String> { token ->
            loadingDialog.dismiss()
            if (token.isNotEmpty())
                login(token, refreshToken)
        }
        val refreshTokenObserver = Observer<String> { token -> encrypted.putString("jwtRefresh", token).apply() }
        val messageObserver = Observer<String> { message ->
            loadingDialog.dismiss()
            messenger(message)
        }

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.token.observe(this, tokenObserver)
        loginViewModel.refreshToken.observe(this, refreshTokenObserver)
        loginViewModel.message.observe(this, messageObserver)

        if (refreshToken.isNotEmpty()) {
            loginViewModel.getSessionToken(refreshToken)
            loadingDialog.show(supportFragmentManager, "LoadingDialog")
            //TODO: remove this, just for testing
            login("", "")
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
            //TODO: we skip login to test without the need to connect to the server
            //loginViewModel.login(email, password)
            loadingDialog.show(supportFragmentManager, "LoadingDialog")
            login("", "")
        }

        findViewById<TextView>(R.id.signInTextView).setOnClickListener() {
            signIn()
        }
    }

    private fun login(sessionToken: String, refreshToken: String) {
        AccessTokenRepository.token = sessionToken
        AccessTokenRepository.refreshToken = refreshToken
        AccessTokenRepository.logout = false
        Log.i("Login", "$sessionToken\t$refreshToken")
        val email = findViewById<EditText>(R.id.emailEditText).text.toString()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL, email)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //TODO: see if its actually useful to send information from the Login View
        }
        loadingDialog.dismiss()
        startActivity(intent)
    }

    private fun signIn() {
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
    }

    private fun logout(encrypted: SharedPreferences.Editor) { encrypted.putString("jwtRefresh", "").apply() }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}