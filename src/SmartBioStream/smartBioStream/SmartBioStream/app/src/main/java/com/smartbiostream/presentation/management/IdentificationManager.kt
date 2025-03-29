package com.smartbiostream.presentation.management

import android.content.Context
import android.content.SharedPreferences


/**
 * Singleton object that manages the type of identification
 */
object IdentificationManager {
    private const val PREFS_NAME = "IDManagerPrefs" // Name of the SharedPreferences file.
    private const val KEY_IDENTIFICATION_USER = "user" // Key for storing the toggle.

    // SharedPreferences instance to store and retrieve data.
    private lateinit var sharedPreferences: SharedPreferences

    // Stores if the identification is by username/password or ID.
    private var isIdentificationUsername: Boolean = true

    // Values for the identification.
    private var ID = ""
    private var username: String? = null
    private var password: String? = null

    /**
     * Initializes the UrlManager with the application context.
     * Loads previously saved values from SharedPreferences, or sets default values if none exist.
     *
     * @param context The application context.
     */
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isIdentificationUsername = sharedPreferences.getBoolean(KEY_IDENTIFICATION_USER, true)
    }

    /**
     * Configure the identification method
     *
     * @param identificationMethod `true` if the identification is by username/password, `false` identifier.
     */
    fun setUsernameIdentification(identificationMethod: Boolean) {
        isIdentificationUsername = identificationMethod
        sharedPreferences.edit().putBoolean(KEY_IDENTIFICATION_USER, isIdentificationUsername)
            .apply()
    }

    /**
     * Updates and persists the ID for the identification
     *
     * @param id The new id to set.
     */
    fun setID(id: String) {
        ID = id
    }

    /**
     * Configures the identification method.
     *
     * @param identificationMethod `true` if the identification is by ID, `false` username/password.
     */
    fun setIDIdentification(identificationMethod: Boolean) {
        isIdentificationUsername = !identificationMethod
        sharedPreferences.edit().putBoolean(KEY_IDENTIFICATION_USER, isIdentificationUsername)
            .apply()
    }

    /**
     * Checks if the identification is by username/password.
     *
     * @return `true` if the identification is by username/password, `false` otherwise.
     */
    fun isIdentificationUsername(): Boolean {
        return isIdentificationUsername
    }

    /**
     * Retrieves the current ID.
     *
     * @return The current set ID.
     */
    fun getID(): String {
        return ID
    }

    /**
     * Checks if the identification is by ID.
     *
     * @return `true` if the identification is by ID, `false` username/password.
     */
    fun isIdentificationID(): Boolean {
        return !isIdentificationUsername
    }

    /**
     * Sets the username.
     *
     * @param currentUsername The username to set.
     */
    fun setUsername(currentUsername: String) {
        username = currentUsername
    }

    /**
     * Sets the password.
     *
     * @param currentPassword The password to set.
     */
    fun setPassword(currentPassword: String) {
        password = currentPassword
    }

    /**
     * Gets the current username.
     *
     * @return The current username, or null if not set.
     */
    fun getUsername(): String? {
        return username
    }

    /**
     * Gets the current password.
     *
     * @return The current password, or null if not set.
     */
    fun getPassword(): String? {
        return password
    }

}
