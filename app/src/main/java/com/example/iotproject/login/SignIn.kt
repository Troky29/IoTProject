package com.example.iotproject.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.Constants
import com.example.iotproject.LoadingDialog
import com.example.iotproject.R

class SignIn : AppCompatActivity() {
    private val loadingDialog by lazy { LoadingDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val messageObserver = Observer<String> { message ->
            messenger(message)
        }
        val loadingObserver = Observer<Boolean> { loading ->
            if (loading)
                loadingDialog.show(supportFragmentManager, "LoadingDialog")
            else
                loadingDialog.dismiss()
        }

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.message.observe(this, messageObserver)
        loginViewModel.loading.observe(this, loadingObserver)

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            val nickname = findViewById<EditText>(R.id.nicknameEditText).text.toString()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val repeatPassword = findViewById<EditText>(R.id.repeatPasswordEditText).text.toString()

            if (nickname.isEmpty()) {
                messenger("Insert a nickname")
                return@setOnClickListener
            }
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
            if (repeatPassword.isEmpty()) {
                messenger("Repeat the password")
            }
            if (password == repeatPassword) {
                loginViewModel.signIn(nickname, email, password)
            } else {
                messenger("The passwords don't match!")
                return@setOnClickListener
            }
        }
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message == Constants.success)
            finish()
    }
}