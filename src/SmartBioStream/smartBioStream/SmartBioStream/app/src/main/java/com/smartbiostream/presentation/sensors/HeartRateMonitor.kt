package com.smartbiostream.presentation.sensors

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartbiostream.presentation.communication.DataSend
import com.smartbiostream.presentation.management.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar

/**
 * Class used to manage the heart rate data.
 * It integrates with the HeartRateSensorManager to collect, store,
 * and send heart rate measurements.
 */
class HeartRateMonitor(private val healthServicesRepository: HeartRateSensorManager) : ViewModel(), InterfaceSensorManager {
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val hr: MutableState<Double> = mutableDoubleStateOf(0.0)
    private val availability: MutableState<DataTypeAvailability> = mutableStateOf(DataTypeAvailability.UNKNOWN)
    private val server = DataSend()
    private val measurements = mutableListOf<Double>()
    private val timestamps = mutableListOf<Long>()

    private val uiState: MutableState<UiState> = mutableStateOf(UiState.Startup)

    /**
     * Launch the sensor if the smartwatch has the heart rate sensor
     */
    init {
        viewModelScope.launch {
            val supported = healthServicesRepository.hasHeartRateCapability()
            uiState.value = if (supported) {
                UiState.Supported
            } else {
                UiState.NotSupported
            }
        }

        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    healthServicesRepository.heartRateMeasureFlow()
                        .takeWhile { enabled.value && SensorManager.isHR()}
                        .collect { measureMessage ->
                            when (measureMessage) {
                                is MeasureMessage.MeasureData -> {
                                    hr.value = measureMessage.data.last().value
                                    Timber.tag("HR").d(hr.value.toString())
                                    if (hr.value > 0.0) {
                                        val calendar = Calendar.getInstance()
                                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                                        val timestampStart = calendar.timeInMillis
                                        timestamps.add(timestampStart)
                                        measurements.add(hr.value)
                                    }
                                    if (measurements.size == 20) {
                                        server.sendHeartRate(measurements, timestamps)
                                        timestamps.clear()
                                        measurements.clear()
                                    }
                                }
                                is MeasureMessage.MeasureAvailability -> {
                                    availability.value = measureMessage.availability
                                }
                            }
                        }
                }
            }
        }
    }

    /**
     * Manage the enabled/disabled sensor configuration
     */
    fun toggleEnabled() {
        enabled.value = !enabled.value
        if (!enabled.value) {
            availability.value = DataTypeAvailability.UNKNOWN
        }
    }

    /**
     * Send the stored measurements to the server
     */
    override fun sendMeasurementsToServer() {
        if (measurements.isNotEmpty()) {
            server.sendHeartRate(measurements, timestamps)
            timestamps.clear()
            measurements.clear()
        }
    }

    override fun stopListening() {}
}

/**
 * Factory method
 */
class MeasureDataViewModelFactory( private val healthServicesRepository: HeartRateSensorManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeartRateMonitor::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HeartRateMonitor(
                healthServicesRepository = healthServicesRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * User interface state
 */
sealed class UiState {
    object Startup : UiState()
    object NotSupported : UiState()
    object Supported : UiState()
}