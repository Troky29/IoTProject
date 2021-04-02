package com.example.iotproject

import okhttp3.MediaType.Companion.toMediaType

class Constants {
    companion object {
        //Localhost
        const val URL = "http://10.0.2.2:5000/api/v1/"
        //Google Cloud Api service
        //const val URL = "https://api-dot-quiet-groove-306310.appspot.com/api/v1/"
        const val FCM_URL = "https://fcm.googleapis.com"
        //TODO: update with the server key of the IoTProject
        const val SERVER_KEY = "AAAAxL7lTsI:APA91bEsVnNzbVM2cZ2rbkl05xSYf7IVMayZpszta2QiCCqgP-8TvtDMzvkQLuQxPJuAlApXRLQQJMsBzH2a7hg9FTboUMNtAmDQktvdjp-_LNk7x7NfzsiE71ETxSd1OknVmlexum3h"
        const val TOPIC = "myTopic"
        //const val CONTENT_TYPE = "application/json"
        val JSON = ("application/json; charset=utf-8").toMediaType()

        const val EMAIL = "com.example.iotproject.EMAIL"

        //These are the used the messages sent throughout the application
        const val server_error = "Error while contacting the server"
        const val invalid_user = "Invalid username/password supplied"
        const val invalid_data = "Invalid input data"
        const val no_gates = "No gates found"
        const val invalid_token = "Invalid token"
        const val success = "Success!"
        const val not_found = "Not found"
//        const val notification_title = "Check new activity"
//        const val notification_content = "New activity detected from your smart gate, what do you want to do?"
        const val destroyed = "Destroyed!"
    }
}


