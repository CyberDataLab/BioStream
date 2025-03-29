package com.smartbiostream.presentation.communication

import android.annotation.SuppressLint
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.management.TokenManager
import com.smartbiostream.presentation.management.UrlManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Class that handles sending measurements to the server.
 */
class DataSend {


    private val clientUnsafe = getUnsafeOkHttpClient()
    private val client = OkHttpClient()
    private var baseUrl = UrlManager.getRequestType() + "://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getMeasurementsURL()

    /**
     * Create OkHttp client that ignores all SSL/TLS security validations.
     *
     * @return unsafe OkHttp
     **/
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
     * Method to send heart rate measurements to the server.
     *
     * @param measurements Heart rate values
     * @param timestamps Timestamps of each heart rate measurement
     **/
    fun sendHeartRate(measurements: MutableList<Double>, timestamps: MutableList<Long>) {

        // Create a JSON array with simulated measurements
        val listData = JSONArray()

        for (i in measurements.indices) {
            val measurement = JSONObject()
            measurement.put("date", timestamps[i])
            measurement.put("type", "heart_rate")
            measurement.put("value", measurements[i])
            listData.put(measurement)
        }

        // Create the JSON object containing the array of measurements
        val json = JSONObject()
        json.put("identifier", IdentificationManager.getID())
        json.put("measurements", listData)

        measurementsToServer(json)
    }

    /**
     * Method to send accelerometer measurements to the server.
     *
     * @param triples Accelerometer values (x, y ,z)
     * @param timestamps Timestamps of each measurement
     **/
    fun sendAccelerometer(triples: List<Triple<Float, Float, Float>>, timestamps: MutableList<Long>) {

        val listData = JSONArray()
        var count = 0

        for (triple in triples) {
            val (x, y, z) = triple
            val xJSON = JSONObject()
            xJSON.put("date", timestamps[count])
            xJSON.put("type", "acc-x")
            xJSON.put("value", x)
            listData.put(xJSON)
            val yJSON = JSONObject()
            yJSON.put("date", timestamps[count])
            yJSON.put("type", "acc-y")
            yJSON.put("value", y)
            listData.put(yJSON)
            val zJSON = JSONObject()
            zJSON.put("date", timestamps[count])
            zJSON.put("type", "acc-z")
            zJSON.put("value", z)
            listData.put(zJSON)
            count += 1
        }

        val json = JSONObject()
        json.put("identifier", IdentificationManager.getID())
        json.put("measurements", listData)


        measurementsToServer(json)
    }

    /**
     * Method to send gyroscope measurements to the server.
     *
     * @param triples Gyroscope values (x, y ,z)
     * @param timestamps Timestamps of each measurement
     **/
    fun sendGyroscope(triples: List<Triple<Float, Float, Float>>, timestamps: MutableList<Long>) {

        val listData = JSONArray()
        var count = 0

        for (triple in triples) {
            val (x, y, z) = triple
            val xJSON = JSONObject()
            xJSON.put("date", timestamps[count])
            xJSON.put("type", "gyro-x")
            xJSON.put("value", x)
            listData.put(xJSON)
            val yJSON = JSONObject()
            yJSON.put("date", timestamps[count])
            yJSON.put("type", "gyro-y")
            yJSON.put("value", y)
            listData.put(yJSON)
            val zJSON = JSONObject()
            zJSON.put("date", timestamps[count])
            zJSON.put("type", "gyro-z")
            zJSON.put("value", z)
            listData.put(zJSON)
            count += 1
        }

        val json = JSONObject()
        json.put("identifier", IdentificationManager.getID())
        json.put("measurements", listData)

        measurementsToServer(json)
    }

    /**
     * Method to send the temperature values to the server.
     *
     * @param data temperature values
     * @param timestamps Timestamps of each measurement
     **/
    fun sendTemperature(data: List<Float>, timestamps: MutableList<Long>) {
        // Create a JSON array with simulated measurements
        val listData = JSONArray()

        for (i in data.indices) {
            val measurement = JSONObject()
            measurement.put("date", timestamps[i])
            measurement.put("type", "temperature")
            measurement.put("value", data[i])
            listData.put(measurement)
        }

        // Create the JSON object containing the array of measurements
        val json = JSONObject()
        json.put("identifier", IdentificationManager.getID())
        json.put("measurements", listData)

        measurementsToServer(json)

    }

    /**
     * Method to send a JSON with data to the server.
     *
     * @param json JSON with data
     **/
    private fun measurementsToServer(json : JSONObject) {

        // Retrieve the token from the TokenManager
        val token = TokenManager.getToken()

        // Set up the content type and the request body
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        // Execute the request on a thread to avoid blocking the user interface
        if (UrlManager.getRequestType() == "httpsuns") {
            baseUrl = "https://" + UrlManager.getIP() + ":" + UrlManager.getPort() + UrlManager.getMeasurementsURL()
            // Build the HTTP POST request with the authorization token
            val request = Request.Builder()
                .url(baseUrl)
                .post(requestBody)
                .addHeader("Authorization", "Token $token")
                .build()
            Thread {
                try {
                    val response = clientUnsafe.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Timber.tag("Server Ok").d("Response body: %s", responseBody)
                    } else {
                        val errorBody = response.body?.string()
                        Timber.tag("Error Body").d(errorBody ?: "Error body")
                    }
                } catch (e: IOException) {
                    Timber.tag("Failure").e("Request error: ${e.message}")
                }
            }.start()
        } else {
            // Build the HTTP POST request with the authorization token
            val request = Request.Builder()
                .url(baseUrl)
                .post(requestBody)
                .addHeader("Authorization", "Token $token")
                .build()
            Thread {
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Timber.tag("Server Ok").d("Response body: %s", responseBody)
                    } else {
                        val errorBody = response.body?.string()
                        Timber.tag("Error Body").d(errorBody ?: "Error body")
                    }
                } catch (e: IOException) {
                    Timber.tag("Failure").e("Request error: ${e.message}")
                }
            }.start()
        }
    }
}
