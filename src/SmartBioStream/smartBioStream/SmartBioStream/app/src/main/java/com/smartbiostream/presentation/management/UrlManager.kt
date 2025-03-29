package com.smartbiostream.presentation.management

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton object that manages the server URL, port, and request type.
 * This class uses SharedPreferences to persist settings across app sessions.
 */
object UrlManager {
    private const val PREFS_NAME = "UrlManagerPrefs" // Name of the SharedPreferences file.
    private const val KEY_URL = "url" // Key for storing the URL.
    private const val KEY_PORT = "port" // Key for storing the port.
    private const val KEY_REQUEST = "requestType" // Key for storing the request type (e.g., http or https).

    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences instance to store and retrieve data.

    // Default values for the URL, port, and request type.
    private var serverIP: String = "0.0.0.0"
    private var serverPort: String = "8000"
    private var requestType: String = "http"

    // Constant values for the url
    private const val LOGIN_URL: String = "/measurements-api/login/"
    private const val LOGOUT_URL: String = "/measurements-api/logout/"
    private const val send_URL: String = "/measurements-api/send/"

    /**
     * Initializes the UrlManager with the application context.
     * Loads previously saved values from SharedPreferences, or sets default values if none exist.
     *
     * @param context The application context.
     */
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        serverIP = sharedPreferences.getString(KEY_URL, "0.0.0.0") ?: "0.0.0.0"
        serverPort = sharedPreferences.getString(KEY_PORT, "8000") ?: "8000"
        requestType = sharedPreferences.getString(KEY_REQUEST, "http") ?: "http"
    }

    /**
     * Updates and persists the URL.
     *
     * @param url The new URL to set.
     */
    fun setIP(url: String) {
        serverIP = url
        sharedPreferences.edit().putString(KEY_URL, serverIP).apply()
    }

    /**
     * Updates and persists the port number.
     *
     * @param port The new port to set.
     */
    fun setPort(port: String) {
        serverPort = port
        sharedPreferences.edit().putString(KEY_PORT, serverPort).apply()
    }

    /**
     * Updates and persists the request type.
     *
     * @param type The new request type (e.g., "http" or "https") to set.
     */
    fun setRequestType(type: String) {
        requestType = type
        sharedPreferences.edit().putString(KEY_REQUEST, requestType).apply()
    }

    /**
     * Retrieves the current URL.
     *
     * @return The currently set URL.
     */
    fun getIP(): String {
        return serverIP
    }

    /**
     * Retrieves the current port number.
     *
     * @return The currently set port number.
     */
    fun getPort(): String {
        return serverPort
    }

    /**
     * Retrieves the current request type.
     *
     * @return The currently set request type (e.g., "http" or "https").
     */
    fun getRequestType(): String {
        return requestType
    }

    /**
     * Retrieves the current login URL.
     *
     * @return The currently set login URL.
     */
    fun getLoginURL(): String {
        return LOGIN_URL
    }

    /**
     * Retrieves the current logout URL.
     *
     * @return The currently set logout URL.
     */
    fun getLogoutURL(): String {
        return LOGOUT_URL
    }

    /**
     * Retrieves the current measurements send URL.
     *
     * @return The currently set measurements send URL.
     */
    fun getMeasurementsURL(): String {
        return send_URL
    }
}