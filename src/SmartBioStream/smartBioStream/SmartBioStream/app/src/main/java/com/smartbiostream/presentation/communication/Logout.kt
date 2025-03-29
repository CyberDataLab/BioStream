package com.smartbiostream.presentation.communication

import android.annotation.SuppressLint
import com.smartbiostream.presentation.management.TokenManager
import com.smartbiostream.presentation.management.UrlManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

/**
 * Interface defining callback methods for the logout process.
 */

interface LogoutCallback {
    fun onLogoutSuccess()
    fun onLogoutFailure()
}

/**
 * Class that handles the logout logic.
 */
class Logout {
    private val clientUnsafe = getUnsafeOkHttpClient()
    private val client = OkHttpClient()

    /**
     * OkHttp configuration to accept self-signed certificates.
     */
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a TrustManager that accepts all certificates
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // Accept all client certificates
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // Accept all server certificates

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    // Returns an empty array instead of null
                    return arrayOf()
                }
            })

            // Set up the SSL context
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Set up OkHttpClient
            val sslSocketFactory = sslContext.socketFactory
            return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // Accept any host name
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Method to log out from the server.
     *
     * @param callback Implementation of [LogoutCallback] to handle the logout results.
     */
    fun logoutToServer(callback: LogoutCallback) {
        // // Logout URL
        var url = UrlManager.getRequestType() + "://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getLogoutURL()

        /**
         * If the certificate is self-signed, change the logic
         */
        if (UrlManager.getRequestType() == "httpsuns") {
            url = "https://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getLogoutURL()
            val token = TokenManager.getToken()
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Token $token")
                .build()
            clientUnsafe.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Timber.tag("Failure").e("Request error: %s, URL: %s", e.message, url)
                    e.printStackTrace()
                    callback.onLogoutFailure()
                }

                /**
                 * On response logic
                 */
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body!!.string()
                        try {
                            val jsonObject = JSONObject(responseBody)
                            if (!jsonObject.has("token")) {
                                Timber.tag("Response Error")
                                    .e("The token is not present in the request")
                                callback.onLogoutFailure()
                                return
                            }
                            val jsonToken = jsonObject.getString("token")
                            TokenManager.setToken(jsonToken)
                            callback.onLogoutSuccess()
                        } catch (e: JSONException) {
                            Timber.tag("JSON Error")
                                .e("Error trying to analyze the JSON: %s", e.message)
                            callback.onLogoutFailure()
                        }
                    } else {
                        val errorBody = response.body?.string()
                        Timber.tag("API Error").e("Response Error - Status code: %d, Error: %s", response.code, errorBody ?: "Empty body")
                        callback.onLogoutFailure()
                    }
                }
            })

        } else {
            val token = TokenManager.getToken()
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Token $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Timber.tag("Failure").e("Request error: %s, URL: %s", e.message, url)
                    e.printStackTrace()
                    callback.onLogoutFailure()
                }

                /**
                 * On response logic
                 */
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body!!.string()
                        try {
                            val jsonObject = JSONObject(responseBody)
                            if (!jsonObject.has("token")) {
                                Timber.tag("Response Error")
                                    .e("The token is not present in the request")
                                callback.onLogoutFailure()
                                return
                            }
                            val jsonToken = jsonObject.getString("token")
                            TokenManager.setToken(jsonToken)
                            callback.onLogoutSuccess()
                        } catch (e: JSONException) {
                            Timber.tag("JSON Error")
                                .e("Error trying to analyze the JSON: %s", e.message)
                            callback.onLogoutFailure()
                        }
                    } else {
                        val errorBody = response.body?.string()
                        Timber.tag("API Error").e("Response Error - Status code: %d, Error: %s", response.code, errorBody ?: "Empty body")
                        callback.onLogoutFailure()
                    }
                }
            })
        }
    }
}