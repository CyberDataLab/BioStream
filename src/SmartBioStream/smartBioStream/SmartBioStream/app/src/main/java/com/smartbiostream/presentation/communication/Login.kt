package com.smartbiostream.presentation.communication

import android.annotation.SuppressLint
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.management.TokenManager
import com.smartbiostream.presentation.management.UrlManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLException
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Interface defining callback methods for the login process.
 */
interface LoginCallback {
    fun onLoginSuccess()
    fun onLoginErrorCredentials()
    fun onLoginErrorProtocol()
    fun onLoginErrorAvailability()

    fun onLoginErrorEndpoint()
}

/**
 * Class that handles the login logic.
 */
class Login {

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
     * Logic to log in to the server.
     */
    fun loginToServer(callback: LoginCallback) {
        var url = UrlManager.getRequestType() + "://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getLoginURL()

        val json = JSONObject()
        try {
            json.put("username", IdentificationManager.getUsername())
            json.put("password", IdentificationManager.getPassword())
        } catch (e: JSONException) {
            Timber.tag("JSON Error").e("Error creating the JSON object: %s", e.message)
            callback.onLoginErrorCredentials()
            return
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        /**
         * If the URL is self-signed, change the logic
         */
        if (UrlManager.getRequestType() == "httpsuns") {
            url = "https://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getLoginURL()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            clientUnsafe.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    when(e) {
                        is SSLException -> {
                            Timber.tag("SSLException").e("SSLException: %s", e.message)
                            callback.onLoginErrorProtocol()
                        }
                        else -> {
                            Timber.tag("Failure").e("Request error: %s, URL: %s", e.message, url)
                            callback.onLoginErrorAvailability()
                        }
                    }
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
                                callback.onLoginErrorCredentials()
                                return
                            }
                            val token = jsonObject.getString("token")
                            TokenManager.setToken(token)
                            callback.onLoginSuccess()
                        } catch (e: JSONException) {
                            Timber.tag("JSON Error")
                                .e("Error trying to analyze the JSON: %s", e.message)
                            callback.onLoginErrorCredentials()
                        }
                    } else {
                        val errorBody = response.body?.string()
                        Timber.tag("API Error").e("Response Error - Status code: %s, Error: %s",
                            response.code, errorBody ?: "Empty body")
                        if (response.code == 400) {
                            callback.onLoginErrorCredentials()
                        } else {
                            callback.onLoginErrorEndpoint()
                        }
                    }
                }
            })
        } else {
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    when(e) {
                        is SSLException -> {
                            Timber.tag("SSLException").e("SSLException: %s", e.message)
                            callback.onLoginErrorProtocol()
                        }
                        else -> {
                            Timber.tag("Failure").e("Request error: %s, URL: %s", e.message, url)
                            callback.onLoginErrorAvailability()
                        }
                    }
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
                                callback.onLoginErrorCredentials()
                                return
                            }
                            val token = jsonObject.getString("token")
                            TokenManager.setToken(token)
                            callback.onLoginSuccess()
                        } catch (e: JSONException) {
                            Timber.tag("JSON Error")
                                .e("Error trying to analyze the JSON: %s", e.message)
                            callback.onLoginErrorCredentials()
                        }
                    } else {
                        Timber.tag("API Error").e("Response Error - Status code: %d", response.code)
                        if (response.code == 400) {
                            callback.onLoginErrorCredentials()
                        } else {
                            callback.onLoginErrorEndpoint()
                        }
                    }
                }
            })
        }
    }
}
