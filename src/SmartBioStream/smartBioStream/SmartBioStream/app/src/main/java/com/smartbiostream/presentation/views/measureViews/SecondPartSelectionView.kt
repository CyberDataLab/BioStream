package com.smartbiostream.presentation.views.measureViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.draw.clip
import com.smartbiostream.presentation.management.SensorManager

/**
 * Class representing the view of the second part to choose the sensors.
 */
class SecondPartSelectionView {

    @Composable
    fun SelectionScreen2(onHRClick: () -> Unit, onBackClick: () -> Unit) {
        val blue = Color(0xFF4A90E2)     // Light blue activated
        val darkBlue = Color(0xFF1C3A5A) // Dark blue deactivated

        /**
         * Initial states of the sensors
         */
        var isGyroEnabled by remember { mutableStateOf(SensorManager.isGyro()) }
        var isTempEnabled by remember { mutableStateOf(SensorManager.isTemp()) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sensors",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )

                /**
                 * Switch button to the gyroscope sensor
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Gyroscope",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = isGyroEnabled,
                        onCheckedChange = { checked ->
                            isGyroEnabled = checked
                            if (checked) SensorManager.falseToTrue(2) else SensorManager.trueToFalse(2)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = blue,
                            uncheckedThumbColor = darkBlue,
                            checkedTrackColor = blue.copy(alpha = 0.6f),
                            uncheckedTrackColor = darkBlue.copy(alpha = 0.6f)
                        )
                    )
                }

                /**
                 * Switch button to the temperature sensor
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Temperature",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = isTempEnabled,
                        onCheckedChange = { checked ->
                            isTempEnabled = checked
                            if (checked) SensorManager.falseToTrue(3) else SensorManager.trueToFalse(3)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = blue,
                            uncheckedThumbColor = darkBlue,
                            checkedTrackColor = blue.copy(alpha = 0.6f),
                            uncheckedTrackColor = darkBlue.copy(alpha = 0.6f)
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    /**
                     * Back button
                     */
                    OutlinedButton(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = blue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    /**
                     * Next button
                     */
                    OutlinedButton(
                        onClick = { onHRClick() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = blue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
