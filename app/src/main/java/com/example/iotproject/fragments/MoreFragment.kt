package com.example.iotproject.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.R
import com.example.iotproject.login.Login
import com.example.iotproject.login.LoginViewModel

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.loguotButton).setOnClickListener() { logout() }
    }

    private fun logout() {
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.logout()
        //TODO: attention, we moved the shared preference management inside the Logic Activity, modify logout accordingly
        startActivity(intent)
    }

}