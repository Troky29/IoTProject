package com.example.iotproject

import com.example.iotproject.Constants.Companion.URL
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

//TODO: for my implementation doesn't make sense to pass AccessRepository, unless you wish to divide this file to be more coherent
class AccessTokenInterceptor (private val accessRepository: AccessTokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = accessRepository.token
        val request = newRequestWithAccessToken(chain.request(), accessToken)
        val response = chain.proceed(request)

        if (response.code == 401) {
            val newAccessToken = accessRepository.token
            if (accessToken != newAccessToken) {
                return chain.proceed(newRequestWithAccessToken(request, newAccessToken))
            }

            val updatedAccessToken = accessRepository.refreshAccessToken()
            response.close()
            return chain.proceed(newRequestWithAccessToken(request, updatedAccessToken))
        }
        return response
    }

    private fun newRequestWithAccessToken(request: Request, accessToken : String): Request {
        return request.newBuilder()
                .header("x-access-token", accessToken)
                .build()
    }
}

class AccessTokenAuthenticator (private val accessRepository: AccessTokenRepository) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = accessRepository.token
        if (!isRequestWithAccessToken(response))
            return null
        val newAccessToken = accessRepository.token
        if (accessToken != newAccessToken)
            return newRequestWithAccessToken(response.request, newAccessToken)
        val updatedAccessToken = accessRepository.refreshAccessToken()
        return newRequestWithAccessToken(response.request, updatedAccessToken)
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("x-access-token")
        return header != null
    }

    private fun newRequestWithAccessToken(request: Request, accessToken : String): Request {
        return request.newBuilder()
                .header("x-access-token", accessToken)
                .build()
    }
}

object AccessTokenRepository {
    var token: String = ""
    var refreshToken: String = ""
    var logout = false
    private val client = OkHttpClient()

    fun refreshAccessToken(): String {

        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jwt_refresh", refreshToken)
                .build()

        val request = Request.Builder()
                .url(URL + "jwt")
                .post(requestBody)
                .build()

        client.newCall(request).execute().use { response ->
            if(!response.isSuccessful) throw IOException("Unexpected code $response")
            return JSONObject(response.body!!.string()).get("jwt_token_expiry").toString()
        }
    }
}