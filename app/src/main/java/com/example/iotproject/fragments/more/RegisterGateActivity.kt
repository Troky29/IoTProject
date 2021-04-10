package com.example.iotproject.fragments.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.iotproject.Constants.Companion.NEIGHBOUR_RADIUS
import com.example.iotproject.IoTApplication
import com.example.iotproject.LoadingDialog
import com.example.iotproject.R
import com.example.iotproject.fragments.gate.GateFragmentViewModel
import com.example.iotproject.fragments.gate.GateViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*

class RegisterGateActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val loadingDialog by lazy { LoadingDialog() }
    private var REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gate)

        val viewModel: GateFragmentViewModel by viewModels {
            GateViewModelFactory((application as IoTApplication).repository)
        }
        viewModel.message.observe(this, { message ->
            messenger(message)
        })

        autocompleteLocation()

        findViewById<ImageButton>(R.id.qrCodeImageButton).setOnClickListener {
            scanQR()
        }

        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val name = findViewById<EditText>(R.id.gateNameEditText).text.toString()
            val thoroughfare = findViewById<EditText>(R.id.gateThoroughfareEditText).text.toString()
            val feature = findViewById<EditText>(R.id.gateFeatureEditText).text.toString()
            val locality = findViewById<EditText>(R.id.gateLocalityEditText).text.toString()
            val postalCode = findViewById<EditText>(R.id.gatePostalCodeEditText).text.toString()
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
            if (thoroughfare.isEmpty() || feature.isEmpty() || locality.isEmpty() || postalCode.isEmpty()) {
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

            val locationHelper = LocationHelper(this)
            val location = locationHelper.getNearestLocation(thoroughfare, feature, locality, postalCode)
            for (neighbour in locationHelper.getNeighbours(location, NEIGHBOUR_RADIUS))
                //TODO: enable this only at the end
                //FirebaseMessaging.getInstance().subscribeToTopic(neighbour)
                continue
            loadingDialog.show(supportFragmentManager, "LoadingDialog")
            viewModel.addGate(name, location.address, location.latitude, location.longitude, code)
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
            try {
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0]
                findViewById<EditText>(R.id.gateThoroughfareEditText).setText(address.thoroughfare)
                findViewById<EditText>(R.id.gateFeatureEditText).setText(address.featureName)
                findViewById<EditText>(R.id.gateLocalityEditText).setText(address.locality)
                findViewById<EditText>(R.id.gatePostalCodeEditText).setText(address.postalCode)
                //TODO: delete after you are done
                findViewById<EditText>(R.id.gateCodeEditText).setText("d8c0e668-b59e-455e-af78-77470ba291c5")
                findViewById<EditText>(R.id.gateNameEditText).setText("TestName")
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

    private fun messenger(message: String) {
        loadingDialog.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if(message == "Successfully added gate!")
            finish()
    }
}