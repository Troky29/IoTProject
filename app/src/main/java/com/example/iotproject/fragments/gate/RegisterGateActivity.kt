package com.example.iotproject.fragments.gate

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.iotproject.Constants.Companion.NEIGHBOUR_RADIUS
import com.example.iotproject.IoTApplication
import com.example.iotproject.LoadingDialog
import com.example.iotproject.R
import com.example.iotproject.fragments.more.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import okio.ByteString
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.*

class RegisterGateActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var image: String? = null
    private val loadingDialog by lazy { LoadingDialog() }
    private var REQUEST_CODE = 1
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_QR_CAPTURE = 49374

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gate)

        val viewModel: GateFragmentViewModel by viewModels {
            GateViewModelFactory((application as IoTApplication).repository)
        }
        viewModel.message.observe(this, { message ->
            messenger(message)
        })
        viewModel.loading.observe(this, { loading ->
            if (loading)
                loadingDialog.show(supportFragmentManager, "LoadingDialog")
            else
                loadingDialog.dismiss()
        })

        autocompleteLocation()

        findViewById<ImageButton>(R.id.qrCodeImageButton).setOnClickListener {
            scanQR()
        }

        findViewById<ImageButton>(R.id.cameraImageButton).setOnClickListener() {
            captureImage()
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

            if (thoroughfare.isEmpty() || feature.isEmpty() || locality.isEmpty() || postalCode.isEmpty()) {
                messenger("Insert a location")
                return@setOnClickListener
            }
            val locationHelper = LocationHelper(this)
            val location = locationHelper.getNearestLocation(thoroughfare, feature, locality, postalCode)
            if (location == null){
                messenger("Insert a valid location")
                return@setOnClickListener
            }

            try {
                UUID.fromString(code)
            } catch (e: Exception) {
                messenger("Not a valid gate code!")
                return@setOnClickListener
            }

            for (neighbour in locationHelper.getNeighbours(location, NEIGHBOUR_RADIUS))
                //TODO: enable this only at the end
                //FirebaseMessaging.getInstance().subscribeToTopic(neighbour)
                continue
            viewModel.addGate(name, location.address, location.latitude, location.longitude, code, image)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //TODO use this structure, is more organized
        /*
         super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {}
                REQUEST_QR_CAPTURE -> {}
            }
        }
         */

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageBiteArray = ByteArrayOutputStream()
            //TODO: check for resolution
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,imageBiteArray)
            val byteArray: ByteArray = imageBiteArray.toByteArray()

            image = BigInteger(1, byteArray).toString(16)
            //TODO: test all these option today, i'm stuck
            //image = ByteString.of(*byteArray)
            //Log.i("RegisterGate", image)
            //Log.i("RegisterGate", imageString)
            //image = imageBiteArray.toByteArray()
            //TODO: see if this is correct, or we need to generate a b64 encoded string
            //val base64 = Base64.getEncoder().encodeToString(image)
            //val test: Byte = image[1]
            //Log.i("Register gate", test.toString())
            //val test = image?.let { bytes -> bytes[1] }
            //Log.i("RegisterGate", test)
            //image = data?.extras.get("data") as Bitmap
        }

        if (requestCode == REQUEST_QR_CAPTURE && resultCode == RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents != null)
                findViewById<EditText>(R.id.gateCodeEditText).setText(result.contents)
            else
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
                findViewById<EditText>(R.id.gateCodeEditText).setText(getString(R.string.test_gate_code))
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

    private fun captureImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE)
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            messenger("Failed to capture image")
        }
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if(message == "Successfully added gate!")
            finish()
    }
}