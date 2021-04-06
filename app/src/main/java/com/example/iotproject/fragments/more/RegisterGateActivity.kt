package com.example.iotproject.fragments.more

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.iotproject.R
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

class RegisterGateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gate)

        val integrator = IntentIntegrator(this).apply {
            setOrientationLocked(false)
            setBeepEnabled(false)
            setPrompt("Scanning QR Code")
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setBarcodeImageEnabled(true)
        }

        findViewById<ImageButton>(R.id.qrCodeImageButton).setOnClickListener {
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null)
                findViewById<EditText>(R.id.qrCodeEditText).text.insert(0, result.contents)
            else
                Log.i("RegisterGateActivity", "Cancelled")
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}