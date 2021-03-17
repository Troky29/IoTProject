package com.example.iotproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.dialog.MaterialDialogs
import com.squareup.okhttp.*
import java.io.IOException

const val EMAIL = "com.example.iotproject.EMAIL"

class Login : AppCompatActivity() {
    val url = "http://10.0.2.2:8080/api/v1/login"
//    val requestQueue = Volley.newRequestQueue(this)
//    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
//            { response ->
//                Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
//            },
//            { error ->
//                //TODO: handle error
//                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
//            }) {
//        @Throws(AuthFailureError::class)
//        override fun getHeaders(): Map<String, String> {
//            val headers = HashMap<String, String>()
//            headers.put("Content-Type", "application/json")
//            return headers
//        }
//    }
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //TODO: check in the preferences if i am already logged, otherwise procede as normal

        findViewById<Button>(R.id.loginButton).setOnClickListener() {
            //TODO: lauch function to check credential, if ok go further
            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            val credential = Credentials.basic(email, password)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", credential)
                .build()

            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(request: Request?, e: IOException?) {
                    println(e.toString())
                }

                override fun onResponse(response: Response?) {
                    when (response?.code()) {
                        200 -> login()
                        401 -> println("Wrong username/password")
                    }
                    println(response?.body().toString())
                }
            })
            //login(it.rootView)

        }
    }

    private fun login() {
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
        val sessionToken = ""
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EMAIL, email)
        }
        startActivity(intent)
    }

    private fun signin() {
        //TODO: add possibility for a user to sign in to the service
    }

    fun messager(context: Context, message: String){
        Handler(Looper.getMainLooper()).post(Runnable {
            fun run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}