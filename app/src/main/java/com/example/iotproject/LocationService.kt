package com.example.iotproject

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.iotproject.Constants.Companion.JSON
import com.google.android.gms.location.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class LocationService : Service() {
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

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.i(TAG, location.toString())
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
        //TODO: update with parameters that you want to send
        val altitude = location!!.altitude.toString()
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        val body = """{"altitude":"$altitude", "latitude:"$latitude", "longitude":"$longitude"}"""
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(Constants.URL + "location")
                .addHeader("x-access-token", AccessTokenRepository.token)
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e (TAG, Constants.server_error)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful)
                when (response.code) {
                    200 -> Log.i(TAG, "Successfully sent location!")
                    400 -> Log.e(TAG, Constants.invalid_data)
                    404 -> Log.e(TAG, Constants.invalid_user)
                    500 -> Log.e(TAG, Constants.server_error)
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