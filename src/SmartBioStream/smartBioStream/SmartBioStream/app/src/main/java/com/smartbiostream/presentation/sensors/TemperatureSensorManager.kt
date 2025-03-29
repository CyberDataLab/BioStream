package com.smartbiostream.presentation.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import com.smartbiostream.presentation.communication.DataSend
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Entry point for TemperatureSensorManager APIs. This also provides methods
 * to register and send temperature data periodically.
 */
class TemperatureSensorManager(private val context: Context): InterfaceSensorManager {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var temperatureListener: SensorEventListener? = null
    private val temperatureList = CopyOnWriteArrayList<Float>()
    private val timestamps = mutableListOf<Long>()
    private val server = DataSend()

    private var lastTemperature: Float? = null
    private var handler: Handler? = null
    private val updateInterval: Long = 1000 // 1 second

    /**
     * Checks if the smartwatch has the temperature sensor.
     */
    private fun hasTemperatureSensor(): Boolean {
        return sensorManager.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE).isNotEmpty()
    }

    /**
     * Starts listening to temperature sensor events and ensures onSensorChanged
     * is called every second, even if the value doesn't change.
     */
    fun startListeningToTemperature() {
        if (!hasTemperatureSensor()) {
            Timber.tag("TemperatureSensor").w("No temperature sensor found. Skipping temperature collection.")
            return
        }

        /**
         * Logic to collect the data
         */
        temperatureListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE && com.smartbiostream.presentation.management.SensorManager.isTemp()) {
                    val temperature = event.values[0]
                    lastTemperature = temperature // Store the last known temperature
                    recordTemperature(temperature)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not necessary for temperature sensor
            }
        }

        /**
         * Register the sensor and start collecting data.
         */
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.let { temperatureSensor ->
            sensorManager.registerListener(
                temperatureListener,
                temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        /**
         * Ensure the first temperature reading is recorded.
         */
        lastTemperature?.let {
            Timber.tag("TemperatureSensor").d("Initial forced temperature record: %s", it)
            recordTemperature(it)
        } ?: Timber.tag("TemperatureSensor").w("No initial temperature reading available.")

        /**
         * Start periodic updates to ensure temperature data is logged every second.
         */
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(object : Runnable {
            override fun run() {
                lastTemperature?.let {
                    Timber.tag("TemperatureSensor").d("Forcing periodic temperature update: %s", it)
                    recordTemperature(it)
                } ?: Timber.tag("TemperatureSensor").w("Skipping periodic update, no temperature recorded yet.")
                handler?.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    /**
     * Records the temperature value and timestamps.
     */
    private fun recordTemperature(temperature: Float) {
        val timestamp = Calendar.getInstance().timeInMillis
        temperatureList.add(temperature)
        timestamps.add(timestamp)

        Timber.tag("TemperatureSensor").d("Recording Temperature: %s°C at %s", temperature, timestamp)

        /**
         * Send data when at least 10 temperature readings have been collected
         */
        if (temperatureList.size >= 20) {
            sendTemperatureData(temperatureList, timestamps)
            temperatureList.clear()
            timestamps.clear()
        }
    }

    /**
     * Stops listening to temperature sensor events and stops periodic updates.
     */
    override fun stopListening() {
        temperatureListener?.let {
            sensorManager.unregisterListener(it)
            temperatureListener = null
        }
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    /**
     * Sends temperature measurements (values and timestamps) to the server.
     * @param data measurements values
     * @param timestamps measurements timestamps
     */
    private fun sendTemperatureData(data: List<Float>, timestamps: MutableList<Long>) {
        server.sendTemperature(data, timestamps)
    }

    /**
     * Sends the collected temperature data to the server.
     */
    override fun sendMeasurementsToServer() {
        if (temperatureList.isNotEmpty()) {
            sendTemperatureData(temperatureList, timestamps)
            temperatureList.clear()
            timestamps.clear()
        }
    }
}
