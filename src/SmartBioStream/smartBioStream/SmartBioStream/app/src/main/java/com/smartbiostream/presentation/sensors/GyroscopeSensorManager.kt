package com.smartbiostream.presentation.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Half.EPSILON
import com.smartbiostream.presentation.communication.DataSend
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resumeWithException
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Entry point for GyroscopeSensorManager APIs. This also provides suspend functions around
 * those APIs to enable use in coroutines.
 */
class GyroscopeSensorManager(private val context: Context): InterfaceSensorManager {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var gyroscopeListener: SensorEventListener? = null
    private val server = DataSend()
    private val triplesList = CopyOnWriteArrayList<Triple<Float, Float, Float>>()
    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f
    private val timestamps = mutableListOf<Long>()

    /**
     * Checks if the smartwatch has the gyroscope sensor
     */
    fun hasGyroscope(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE)
        return sensorList.isNotEmpty()
    }

    /**
     * Start listening to gyroscope sensor events.
     */
    suspend fun startListeningToGyroscope(): Triple<Float, Float, Float> =

        suspendCancellableCoroutine { continuation ->
            gyroscopeListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_GYROSCOPE && com.smartbiostream.presentation.management.SensorManager.isGyro()) {
                        // The delta rotation for this time interval will be multiplied by the current rotation
                        // after being calculated from the gyroscope sample data.
                        if (timestamp != 0f) {
                            val dT = (event.timestamp - timestamp) * NS2S
                            // Axis of the rotation sample, not yet normalized.
                            var axisX: Float = event.values[0]
                            var axisY: Float = event.values[1]
                            var axisZ: Float = event.values[2]

                            // Calculates the angular velocity of the sample
                            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                            // Normalizes the rotation vector if it is large enough to obtain the axis
                            if (omegaMagnitude > EPSILON) {
                                axisX /= omegaMagnitude
                                axisY /= omegaMagnitude
                                axisZ /= omegaMagnitude
                            }

                            /* Integrates around this axis using the angular velocity over the time interval
                            * to obtain a delta rotation for this sample during the time interval.
                            * This axis-angle representation of the delta rotation will be converted
                            * into a quaternion before converting it into a rotation matrix.
                            */
                            val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
                            val sinThetaOverTwo: Float = sin(thetaOverTwo)
                            val cosThetaOverTwo: Float = cos(thetaOverTwo)
                            deltaRotationVector[0] = sinThetaOverTwo * axisX
                            deltaRotationVector[1] = sinThetaOverTwo * axisY
                            deltaRotationVector[2] = sinThetaOverTwo * axisZ
                            deltaRotationVector[3] = cosThetaOverTwo

                            Timber.tag("Gyroscope").d("%s %s %s", deltaRotationVector[0].toString(), deltaRotationVector[1].toString(), deltaRotationVector[2].toString())
                            triplesList.add(Triple(deltaRotationVector[0], deltaRotationVector[1], deltaRotationVector[2]))
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.HOUR_OF_DAY, 1)
                            val timestampStart = calendar.timeInMillis
                            timestamps.add(timestampStart)
                            if (triplesList.size >= 10) {
                                server.sendGyroscope(triplesList, timestamps)
                                triplesList.clear()
                                timestamps.clear()
                            }

                        }
                        timestamp = event.timestamp.toFloat()
                        val deltaRotationMatrix = FloatArray(9) { 0f }
                        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector)

                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Not necessary
                }
            }

            /**
             * Register the sensor and start collecting data
             */
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let { gyroscopeSensor ->
                sensorManager.registerListener(
                    gyroscopeListener,
                    gyroscopeSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } ?: run {
                continuation.resumeWithException(NullPointerException("Gyroscope sensor not found"))
            }

            /**
             * Stop collecting data
             */
            continuation.invokeOnCancellation {
                sendMeasurementsToServer()
                stopListening()
            }
        }

    /**
     * Stops listening to gyroscope sensor events.
     */
    override fun stopListening() {
        gyroscopeListener?.let {
            sensorManager.unregisterListener(it)
            gyroscopeListener = null
        }
    }

    /**
     * Send the stored measurements to the server
     */
    override fun sendMeasurementsToServer() {
        if (triplesList.isNotEmpty()) {
            server.sendGyroscope(triplesList, timestamps)
            triplesList.clear()
            timestamps.clear()
        }
    }
}