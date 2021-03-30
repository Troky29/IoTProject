package com.example.iotproject

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.text.format.Time
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class LocationService : Service() {
    //val mBinder: IBinder = LocalBinder()
    val TAG = "LocationService"

    private val client: OkHttpClient = OkHttpClient().newBuilder()
            .authenticator(AccessTokenAuthenticator(AccessTokenRepository))
            .addInterceptor(AccessTokenInterceptor(AccessTokenRepository))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 1

    private var inProgress = false
    private var serviceAvailable = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    updateLocation(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            //maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                    return

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    fun updateLocation(location: Location?) {
        //TODO: update with parameters that you want ot send
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("altitude", location!!.altitude.toString())
                .addFormDataPart("latitude", location.latitude.toString())
                .addFormDataPart("longitude", location.longitude.toString())
                .build()

        val request = Request.Builder()
                .url(Constants.URL + "location")
                .addHeader("x-access-token", AccessTokenRepository.token)
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e (TAG, Constants.server_error)
                Log.e(TAG, client.toString())
                Log.e(TAG, e.stackTraceToString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful)
                when (response.code) {
                    200 -> {
                        //TODO: since this will be in the background, we do not notify the user
                        Log.i(TAG, "Successfully sent location!")
                    }
                    400 -> { }
                    500 -> { }
                }
                response.close()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, Constants.destroyed)
    }

}