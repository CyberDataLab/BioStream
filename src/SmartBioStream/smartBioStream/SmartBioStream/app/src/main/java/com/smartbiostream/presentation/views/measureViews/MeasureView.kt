package com.smartbiostream.presentation.views.measureViews

import AccelerometerSensorManager
import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.smartbiostream.R
import com.smartbiostream.presentation.communication.Logout
import com.smartbiostream.presentation.communication.LogoutCallback
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.sensors.HeartRateMonitor
import com.smartbiostream.presentation.sensors.MeasureDataViewModelFactory
import com.smartbiostream.presentation.sensors.TemperatureSensorManager
import com.smartbiostream.presentation.sensors.GyroscopeSensorManager
import com.smartbiostream.presentation.sensors.HeartRateSensorManager
import com.smartbiostream.presentation.sensors.InterfaceSensorManager
import timber.log.Timber

/**
 * View used to display the measure screen
 */
class MeasureView {

    private val green = Color(0xFF4CAF50)  // Green (Activated measuring)
    private val orange = Color(0xFFFF9800) // Yellow (Paused measuring)
    private val gray = Color(0xFF888888)   // Gray for inferior buttons


    val logout = Logout()

    /**
     * Function used to display the screen
     */
    @SuppressLint("WakelockTimeout", "Wakelock")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MeasureScreen(context: Context, onLoginClick: () -> Unit, onSensorClick: () -> Unit) {
        val heartRateManager = HeartRateSensorManager(context)
        val accelerometerManager = AccelerometerSensorManager(context)
        val gyroscopeManager = GyroscopeSensorManager(context)
        val temperatureManager = TemperatureSensorManager(context)

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        /**
         * Power Manager to prevent the suspense screen
         */
        val wakeLock by remember {
            mutableStateOf(
                powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "MyApp::HeartRateMeasureWakelockTag"
                )
            )
        }

        /**
         * Model to recollect the heart rate data
         */
        val viewModelHeartRate: HeartRateMonitor = viewModel(
            factory = MeasureDataViewModelFactory(heartRateManager)
        )

        /**
         * Container of sensor managers
         * For heart rate, it contains the HeartRateMonitor
         */
        val sensorManagers = listOf<InterfaceSensorManager>(
            accelerometerManager,
            gyroscopeManager,
            temperatureManager,
            viewModelHeartRate
        )

        /**
         * Variable used to know when the model is enabled
         */
        val enabled by viewModelHeartRate.enabled.collectAsState()

        /**
         * WakeLock Management
         */
        DisposableEffect(context) {
            val activity = context as? android.app.Activity
            activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        DisposableEffect(enabled) {
            if (enabled && !wakeLock.isHeld) {
                wakeLock.acquire()
            } else if (!enabled && wakeLock.isHeld) {
                wakeLock.release()
            }

            onDispose {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }

        /**
         * Variable used to request permission to the user
         */
        val permissionState = rememberPermissionState(
            permission = android.Manifest.permission.BODY_SENSORS,
            onPermissionResult = { granted ->
                if (granted) viewModelHeartRate.toggleEnabled()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if (enabled) "Measuring" else "Paused",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * Start/Stop measuring button
             */
            Button(
                onClick = {
                    if (permissionState.status.isGranted) {
                        viewModelHeartRate.toggleEnabled()
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (enabled) orange else green,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = if (enabled) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play),
                    contentDescription = "Toggle Measurement",
                    modifier = Modifier.size(50.dp) // Icon size scaled with button
                )
            }

            /**
             * Launched effect to start/stop the accelerometer sensor
             */
            LaunchedEffect(enabled) {
                if (enabled && accelerometerManager.hasAccelerometer(context)) {
                    accelerometerManager.startListeningToAccelerometer()
                }
                else {
                    accelerometerManager.stopListening()
                    accelerometerManager.sendMeasurementsToServer()
                }
            }

            /**
             * Launched effect to start/stop the gyroscope sensor
             */
            LaunchedEffect(enabled) {
                if (enabled) {
                    gyroscopeManager.startListeningToGyroscope()
                }
                else {
                    gyroscopeManager.stopListening()
                    gyroscopeManager.sendMeasurementsToServer()
                }
            }

            /**
             * Launched effect to start/stop the temperature sensor
             */
            DisposableEffect(enabled) {
                if (enabled) {
                    temperatureManager.startListeningToTemperature()
                }
                onDispose {
                    temperatureManager.stopListening()
                    temperatureManager.sendMeasurementsToServer()
                }
            }


            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                /**
                 * Select sensors button
                 */
                Button(
                    onClick = {
                        if (enabled) viewModelHeartRate.toggleEnabled()
                        onSensorClick()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = gray,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.List, contentDescription = "Select Sensors")
                }

                Spacer(modifier = Modifier.width(24.dp))

                /**
                 * Main menu button
                 */
                Button(
                    onClick = {
                        sensorManagers.forEach {
                            it.stopListening()
                            it.sendMeasurementsToServer()
                        }
                        if (enabled) viewModelHeartRate.toggleEnabled()

                        if (IdentificationManager.isIdentificationUsername()) {
                            // server logout
                            logout.logoutToServer(object : LogoutCallback {
                                override fun onLogoutSuccess() {
                                    Timber.tag("Logout").e("Success")
                                }
                                override fun onLogoutFailure() {
                                    Timber.tag("Logout").e("Failure")
                                }
                            })
                        }

                        onLoginClick()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = gray,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Main Menu")
                }
            }
        }
    }
}
