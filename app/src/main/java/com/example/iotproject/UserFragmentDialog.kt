package com.example.iotproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class UserFragmentDialog(private val logoutListener: LogoutListener, var user: User) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        view.findViewById<TextView>(R.id.nicknameTextView).text = user.nickname

        view.findViewById<TextView>(R.id.emailTextView).text = user.email

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            logoutListener.logout()
            dismiss()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface LogoutListener {
        fun logout()
    }
}