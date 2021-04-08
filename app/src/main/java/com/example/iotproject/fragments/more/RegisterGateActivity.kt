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
import kotlin.math.PI
import kotlin.math.cos

class RegisterGateActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "RegisterGateActivity"
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
            val thoroughfare = findViewById<EditText>(R.id.gateThoroughfareEditText).text.toString()
            val feature = findViewById<EditText>(R.id.gateFeatureEditText).text.toString()
            val locality = findViewById<EditText>(R.id.gateLocalityEditText).text.toString()
            val postcode = findViewById<EditText>(R.id.gatePostalCodeEditText).text.toString()
            val location = "$thoroughfare, $feature, $locality, $postcode"
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
            if (thoroughfare.isEmpty() || feature.isEmpty() || locality.isEmpty() || postcode.isEmpty()) {
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
            //TODO: you should also send information about the near addresses
            val neighbours = getNeighbours(location)
            //TODO: correct everything down the line, compose in some way a single string of the gate location
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
            val neighbours: MutableList<String> by lazy { mutableListOf() }
            try {
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0]
                findViewById<EditText>(R.id.gateThoroughfareEditText).setText(address.thoroughfare)
                findViewById<EditText>(R.id.gateFeatureEditText).setText(address.featureName)
                findViewById<EditText>(R.id.gateLocalityEditText).setText(address.locality)
                findViewById<EditText>(R.id.gatePostalCodeEditText).setText(address.postalCode)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    //TODO: see if you actually want to implement this, probably in its own function, given the fact that you have to search inside a radius (probably just combine 8 points around)

    //TODO: once the neighbours are found subscribe to the appropriate topics
    private fun getNeighbours(location: String) : MutableList<String> {
        val geocoder = Geocoder(this, Locale.getDefault())
        //val addresses: List<String>
        val neighbours: MutableList<String> by lazy { mutableListOf() }

        try {
            val coordinates  = geocoder.getFromLocationName(location, 1)[0]
            val earth = 6371
            val longitudeMeter = ((180/PI) / earth / cos(coordinates.latitude * PI / 180)) / 1000
            val latitudeMeter = ((180 / PI) / earth) / 1000
            val addresses = geocoder.getFromLocation(
                    coordinates.latitude + latitudeMeter * 50,
                    coordinates.longitude + longitudeMeter * 50,
                    5)
            Log.i(TAG, addresses.toString())
            for (address in addresses) {
                val thoroughfare = address.thoroughfare
                if (thoroughfare.isNullOrEmpty() || thoroughfare == "Unnamed Road")
                    continue
                if (!neighbours.contains(thoroughfare))
                    neighbours.add(thoroughfare)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }

        //TODO: handle this neighbours thoroughfare, probably we should couple it with the other information, to avoid uncertainty
        Log.i("RegisterGateActivity", "Neighbours: $neighbours")

        return neighbours
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