package com.example.iotproject

class Constants {
    companion object {
        const val URL = "http://10.0.2.2:8080/api/v1/"
        const val FCM_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAxL7lTsI:APA91bEsVnNzbVM2cZ2rbkl05xSYf7IVMayZpszta2QiCCqgP-8TvtDMzvkQLuQxPJuAlApXRLQQJMsBzH2a7hg9FTboUMNtAmDQktvdjp-_LNk7x7NfzsiE71ETxSd1OknVmlexum3h"
        const val CONTENT_TYPE = "application/json"

        const val EMAIL = "com.example.iotproject.EMAIL"

        const val server_error = "Error while contacting the server"
        const val invalid_user = "Invalid username/password supplied"
        const val invalid_data = "Invalid input data"
        const val no_gates = "No gates found"
        const val notification_title = "Check new activity"
        const val notification_content = "New activity detected from your smart gate, what do you want to do?"
    }
}


