package com.smartbiostream.presentation.management

/**
 * Singleton object that manages the authentication token.
 */
object TokenManager {
    /**
     * Stores the authentication token.
     */
    private var authToken: String? = null

    /**
     * Sets the authentication token.
     *
     * @param token The authentication token to set.
     */
    fun setToken(token: String) {
        authToken = token
    }

    /**
     * Gets the current authentication token.
     *
     * @return The current authentication token or null if not set.
     */
    fun getToken(): String? {
        return authToken
    }
}
