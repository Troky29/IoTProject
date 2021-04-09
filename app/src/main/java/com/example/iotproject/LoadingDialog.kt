package com.example.iotproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class LoadingDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setCancelable(false)
    }
}