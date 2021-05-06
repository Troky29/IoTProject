package com.example.iotproject

import okhttp3.MediaType.Companion.toMediaType

class Constants {
    companion object {
        //Localhost
        //const val URL = "http://10.0.2.2:5000/api/v1/"
        //Google Cloud Api service
        const val URL = "https://api-dot-quiet-groove-306310.appspot.com/api/v1/"
        val JSON = ("application/json; charset=utf-8").toMediaType()

        //These are the used the messages sent throughout the application
        const val server_error = "Error while contacting the server"
        const val invalid_user = "Invalid username/password supplied"
        const val invalid_data = "Invalid input data"
        const val success = "Success!"
        const val destroyed = "Destroyed!"
        const val EARTH = 6371  //Radius of the earth in Km, used for position calculation
        const val NEIGHBOUR_RADIUS = 50 //Radius to check for finding neighbours in meter
        enum class State{ ALLOW, DENY, REPORT, IGNORE }
    }
}