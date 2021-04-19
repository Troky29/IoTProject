package com.example.iotproject.fragments.gate

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.iotproject.Constants.Companion.EARTH
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class LocationHelper(val context: Context) {
    val geocoder = Geocoder(context, Locale.getDefault())

    fun getNearestLocation(thoroughfare: String, feature: String, locality: String, postalCode: String): LocationInfo? {
        val query = "$thoroughfare, $feature, $postalCode, $locality"
        //We try to check if the query given by the user bears an actual location
        return try {
            val location = geocoder.getFromLocationName(query, 1)[0]
            if (location.thoroughfare.isNullOrEmpty() || location.featureName.isNullOrEmpty() ||
                    location.locality.isNullOrEmpty() || location.postalCode.isNullOrEmpty())
                null
            else
                LocationInfo(location.getAddressLine(0), location.latitude, location.longitude)
        } catch (e: Exception) {
            Log.e("LocationHelper", e.toString())
            null
        }
    }

    fun getNeighbours(location: LocationInfo, meterRadius: Int) : MutableList<String> {
        val neighbours: MutableList<String> by lazy { mutableListOf() }
        try {
            for ((latitude, longitude) in getRadiusCoordinates(location.latitude, location.longitude, meterRadius))
                for (address in geocoder.getFromLocation(latitude, longitude, 5)) {
                    val thoroughfare = address.thoroughfare
                    val postalCode = address.postalCode
                    if (thoroughfare.isNullOrEmpty() || thoroughfare == "Unnamed Road" || postalCode.isNullOrEmpty())
                        continue
                    val neighbour = "$thoroughfare $postalCode"
                    if(!neighbours.contains(neighbour))
                        neighbours.add(neighbour)
                }
        } catch (e: Exception) {
            Log.e("LocationHelper", e.message.toString())
        }
        return neighbours
    }

    //TODO: this approximation results a bit flattened on the longitude, see if we need a better approach
    private fun latitudeToMeter(latitude: Double): Double {
        return ((180/PI) / EARTH / cos(latitude * PI / 180)) / 1000
    }

    private fun longitudeToMeter(): Double {
        return ((180 / PI) / EARTH) / 1000
    }

    //We calculate some coordinates around the specified radius(m) to check for possible neighbours
    private fun getRadiusCoordinates(latitude: Double, longitude: Double, radius: Int): Set<Pair<Double, Double>> {
        val latitudeRadius = latitudeToMeter(latitude) * radius
        val longitudeRadius = longitudeToMeter() * radius

        return setOf(
                Pair(latitude + latitudeRadius, longitude),
                Pair(latitude + (sin(PI / 4) * latitudeRadius), longitude + (cos(PI / 4) * longitudeRadius)),
                Pair(latitude, longitude + longitudeRadius),
                Pair(latitude - (sin(PI / 4) * latitudeRadius), longitude + (cos(PI / 4) * longitudeRadius)),
                Pair(latitude - latitudeRadius, longitude),
                Pair(latitude - (sin(PI / 4) * latitudeRadius), longitude - (cos(PI / 4) * longitudeRadius)),
                Pair(latitude, longitude - longitudeRadius),
                Pair(latitude + (sin(PI / 4) * latitudeRadius), longitude - (cos(PI / 4) * longitudeRadius))
        )
    }

    data class LocationInfo(val address: String, val latitude: Double, val longitude: Double)
}