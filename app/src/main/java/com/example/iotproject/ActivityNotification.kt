package com.example.iotproject

import android.app.Notification
import androidx.core.app.NotificationCompat
import com.example.iotproject.Constants.Companion.CONTENT_TYPE
import com.example.iotproject.Constants.Companion.FCM_URL
import com.example.iotproject.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


class ActivityNotification {

}

data class  NotificationData(val name: String, val message: String)

data class PushNotification(val data: NotificationData, val to: String)

interface NotificationAPI {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST ("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(FCM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}