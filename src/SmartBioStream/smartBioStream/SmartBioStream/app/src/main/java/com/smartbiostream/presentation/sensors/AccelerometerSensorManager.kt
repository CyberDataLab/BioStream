import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.smartbiostream.presentation.communication.DataSend
import com.smartbiostream.presentation.sensors.InterfaceSensorManager
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resumeWithException

/**
 * Entry point for AccelerometerSensorManager APIs.
 * This also provides suspend functions around
 * those APIs to enable use in coroutines.
 */
class AccelerometerSensorManager(private val context: Context): InterfaceSensorManager {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * Sensor Listener for the accelerometer
     */
    private var accelerometerListener: SensorEventListener? = null
    private val triplesList = CopyOnWriteArrayList<Triple<Float, Float, Float>>()
    private val timestamps = mutableListOf<Long>()
    private val server = DataSend()

    /**
     * Checks if the smartwatch has the accelerometer sensor
     */
    fun hasAccelerometer(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
        return sensorList.isNotEmpty()
    }

    /**
     * Start listening to accelerometer sensor events.
     */
    suspend fun startListeningToAccelerometer(): Triple<Float, Float, Float> =
        suspendCancellableCoroutine { continuation ->
            accelerometerListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && com.smartbiostream.presentation.management.SensorManager.isAcc()) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                        val timestampStart = calendar.timeInMillis
                        timestamps.add(timestampStart)
                        triplesList.add(Triple(x, y, z))
                        Timber.tag("Accelerometer")
                            .d("%s %s %s", x, y, z)
                        if (triplesList.size >= 50) {
                            server.sendAccelerometer(triplesList, timestamps)
                            triplesList.clear()
                            timestamps.clear()
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Not needed for accelerometer
                }
            }

            /**
             * Register the sensor and start collecting data
             */
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { accelerometerSensor ->
                sensorManager.registerListener(
                    accelerometerListener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } ?: run {
                continuation.resumeWithException(NullPointerException("Accelerometer sensor not found"))
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
     * Stop listening to accelerometer sensor events.
     */
    override fun stopListening() {
        accelerometerListener?.let {
            sensorManager.unregisterListener(it)
            accelerometerListener = null
        }
    }

    /**
     * Send the stored measurements to the server
     */
    override fun sendMeasurementsToServer() {
        if (triplesList.isNotEmpty()) {
            server.sendAccelerometer(triplesList, timestamps)
            triplesList.clear()
            timestamps.clear()
        }
    }
}