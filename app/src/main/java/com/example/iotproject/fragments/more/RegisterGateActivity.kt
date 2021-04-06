package com.example.iotproject.fragments.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.MainActivityViewModel
import com.example.iotproject.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*

class RegisterGateActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gate)

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        autocompleteLocation()
        //TODO: possibly we could autocomplete the location field
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
            if (name.length > 30) {
                messenger("Gate name is too long (MAX 30 characters)")
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
            //TODO: when you have decided what to do with viewmodels, observe a variable and return only after having received a reply
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

    private fun autocompleteLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>
            try {
                addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                findViewById<EditText>(R.id.gateLocationEditText).setText(
                    addresses[0].getAddressLine(0))
                Log.i("RegisterGateActivity", addresses.toString())
            } catch (e: Exception) {
                Log.e("RegisterGateActivity", e.message.toString())
            }
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