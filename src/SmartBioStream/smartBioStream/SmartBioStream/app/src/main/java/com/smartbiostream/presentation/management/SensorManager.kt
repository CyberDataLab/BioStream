package com.smartbiostream.presentation.management

/**
 * Singleton object that manages access to the sensors.
 */
object SensorManager {
    /**
     * Stores the access state for the sensors.
     * Index mapping:
     * 0 - Heart Rate Sensor
     * 1 - Accelerometer
     * 2 - Gyroscope
     * 3 - Temperature
     */
    private val booleans = booleanArrayOf(false, false, false, false)

    /**
     * Sets the value at the specified index to true.
     *
     * @param index The index of the array to set to true.
     */
    fun falseToTrue(index: Int) {
        booleans[index] = true
    }

    /**
     * Sets the value at the specified index to false.
     *
     * @param index The index of the array to set to false.
     */
    fun trueToFalse(index: Int) {
        booleans[index] = false
    }

    /**
     * Checks if the Heart Rate Sensor is active.
     *
     * @return `true` if the Heart Rate Sensor is active, `false` otherwise.
     */
    fun isHR(): Boolean {
        return booleans[0]
    }

    /**
     * Checks if the Accelerometer is active.
     *
     * @return `true` if the Accelerometer is active, `false` otherwise.
     */
    fun isAcc(): Boolean {
        return booleans[1]
    }

    /**
     * Checks if the Gyroscope is active.
     *
     * @return `true` if the Gyroscope is active, `false` otherwise.
     */
    fun isGyro(): Boolean {
        return booleans[2]
    }

    /**
     * Checks if the temperature is active.
     *
     * @return `true` if the Temperature is active, `false` otherwise.
     */
    fun isTemp(): Boolean {
        return booleans[3]
    }
}