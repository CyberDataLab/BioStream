package com.smartbiostream.presentation.communication
import com.smartbiostream.presentation.management.UrlManager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.SSLException

/**
 * Class used to check the URLs that the app will use
 */
class CheckURL {

    /**
     * Function to check the reachability of the login URL
     */
    suspend fun checkLogin(): Int {
        val url = if (UrlManager.getRequestType() == "httpsuns") {
            "https://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getLoginURL()}"
        } else {
            "${UrlManager.getRequestType()}://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getLoginURL()}"
        }

        return try {
            withContext(Dispatchers.IO) { // Thread
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 2000 // 2 seconds
                connection.readTimeout = 2000
                connection.connect()


                val responseCode = connection.responseCode
                Timber.tag("CheckURLLogin").d("Server Response Code: %s", responseCode)

                connection.disconnect()

                responseCode
            }
        } catch (e: SSLException) {
            Timber.tag("CheckURL").e(e, "Error SSL/TLS")
            -1 // SSL/TSL code
        } catch (e: SocketTimeoutException) {
            Timber.tag("CheckURL").e(e, "Timeout")
            -2 // Timeout code
        } catch (e: Exception) {
            Timber.tag("CheckURL").e(e, "Undefined exception")
            -2 // Timeout code
        }
    }

    /**
     * Function to check the reachability of the measurements send URL
     */
    suspend fun checkSendMeasurements() : Int {
        val url = if (UrlManager.getRequestType() == "httpsuns") {
            "https://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getMeasurementsURL()}"
        } else {
            "${UrlManager.getRequestType()}://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getMeasurementsURL()}"
        }

        return try {
            withContext(Dispatchers.IO) { // Thread
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 2000 // 2 seconds
                connection.readTimeout = 2000
                connection.connect()

                val responseCode = connection.responseCode
                Timber.tag("CheckURLMeasurements").d("Server Response Code: %s", responseCode)

                connection.disconnect()

                responseCode
            }
        } catch (e: SSLException) {
            Timber.tag("CheckURL").e(e, "Error SSL/TLS")
            -1 // SSL/TSL code
        } catch (e: SocketTimeoutException) {
            Timber.tag("CheckURL").e(e, "Timeout")
            -2 // Timeout code
        } catch (e: Exception) {
            Timber.tag("CheckURL").e(e, "Undefined exception")
            -2 // Timeout code
        }
    }

    /**
     * Function to check the reachability of the logout URL
     */
    suspend fun checkLogout() : Int {
        val url = if (UrlManager.getRequestType() == "httpsuns") {
            "https://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getLogoutURL()}"
        } else {
            "${UrlManager.getRequestType()}://${UrlManager.getIP()}:${UrlManager.getPort()}${UrlManager.getLogoutURL()}"
        }

        return try {
            withContext(Dispatchers.IO) { // Thread
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connectTimeout = 2000 // 2 seconds
                connection.readTimeout = 2000
                connection.connect()

                val responseCode = connection.responseCode
                Timber.tag("CheckURL").d("Server Response Code: %s", responseCode)

                connection.disconnect()

                // If the server respond within de codes 200...400, it is reachable
                responseCode
            }
        } catch (e: SSLException) {
            Timber.tag("CheckURL").e(e, "Error SSL/TLS")
            -1 // SSL/TSL code
        } catch (e: SocketTimeoutException) {
            Timber.tag("CheckURL").e(e, "Timeout")
            -2 // Timeout code
        } catch (e: Exception) {
            Timber.tag("CheckURL").e(e, "Undefined exception")
            -2 // Timeout code
        }

    }
}