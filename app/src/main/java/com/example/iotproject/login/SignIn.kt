package com.example.iotproject.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.R

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val messageObserver = Observer<String> { message -> messenger(message) }

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.message.observe(this, messageObserver)

        findViewById<Button>(R.id.signInButton).setOnClickListener() {
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            val repeatPassword = findViewById<EditText>(R.id.editTextRepeatPassword).text.toString()
            if (password == repeatPassword)
                loginViewModel.signIn(email, password)
        }
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message == "Done!")
            finish()
    }
}