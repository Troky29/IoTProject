package com.example.iotproject.fragments.more

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.MainActivityViewModel
import com.example.iotproject.R
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import java.lang.Exception
import java.util.*

class RegisterGateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gate)

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        findViewById<ImageButton>(R.id.qrCodeImageButton).setOnClickListener {
            scanQR()
        }

        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val name = findViewById<EditText>(R.id.gateNameEditText).text.toString()
            val location = findViewById<EditText>(R.id.gateLocationEditText).text.toString()
            val code = findViewById<EditText>(R.id.gateCodeEditText).text.toString()

            if (name.isEmpty()) {
                messenger("Insert a gate name")
                return@setOnClickListener
            }
            if (name.length > 15) {
                messenger("Gate name is too long (MAX 15 characters)")
                return@setOnClickListener
            }
            //TODO: check for not admissible characters for the name
            if (location.isEmpty()) {
                messenger("Insert a location")
                return@setOnClickListener
            }
            //TODO: check for a valid location
            try {
                UUID.fromString(code)
            } catch (e: Exception) {
                messenger("Not a valid gate code!")
                return@setOnClickListener
            }
            viewModel.addGate(name, location, code)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null)
                findViewById<EditText>(R.id.gateCodeEditText).setText(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanQR() {
        val integrator = IntentIntegrator(this).apply {
            setOrientationLocked(false)
            setBeepEnabled(false)
            setPrompt("Scanning QR Code")
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setBarcodeImageEnabled(true)
        }
        integrator.initiateScan()
    }

    private fun messenger(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}