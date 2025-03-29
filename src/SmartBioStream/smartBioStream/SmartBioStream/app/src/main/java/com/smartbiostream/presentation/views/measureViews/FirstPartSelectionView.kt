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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.draw.clip
import androidx.wear.compose.material.OutlinedButton
import com.smartbiostream.presentation.management.SensorManager

/**
 * View used to display the first part of the available sensors
 */
class FirstPartSelectionView {

    @Composable
    fun SelectionScreen(onNextClick: () -> Unit) {
        val blue = Color(0xFF4A90E2)     // Light blue activated
        val darkBlue = Color(0xFF1C3A5A) // Dark blue deactivated

        /**
         * Initial states of the sensors
         */
        var isHREnabled by remember { mutableStateOf(SensorManager.isHR()) }
        var isAccEnabled by remember { mutableStateOf(SensorManager.isAcc()) }

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
                 * Switch used to activate the heart rate sensor
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Heart rate",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = isHREnabled,
                        onCheckedChange = { checked ->
                            isHREnabled = checked
                            if (checked) SensorManager.falseToTrue(0) else SensorManager.trueToFalse(0)
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
                 * Switch used to activate the accelerometer sensor
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Accelerometer",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = isAccEnabled,
                        onCheckedChange = { checked ->
                            isAccEnabled = checked
                            if (checked) SensorManager.falseToTrue(1) else SensorManager.trueToFalse(1)
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
                 * Next button
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onNextClick() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
