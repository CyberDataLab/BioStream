package com.smartbiostream.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.system.exitProcess

/**
 * Main view used to display the first window of the app
 */
class MainScreenView {

    @Composable
    fun ThirdScreen(onAuthClick: () -> Unit, onOptionsClick: () -> Unit, onConnectionClick: () -> Unit) {
        val blue = Color(0xFF4A90E2) // Light blue
        val density = LocalDensity.current // Details of the screen

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {

            /**
             * Variables used to calculate the Width and Height in order to know if the smartwatch is round or not
             */
            val maxWidthDp = with(density) { constraints.maxWidth.toDp() }.value
            val maxHeightDp = with(density) { constraints.maxHeight.toDp() }.value
            val isRound = maxWidthDp == maxHeightDp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (isRound) 10.dp else 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Home",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = if (isRound) 2.dp else 5.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val buttonSize = (maxWidthDp * 0.25f).dp

                    /**
                     * Start button
                     */
                    Button(
                        onClick = { onAuthClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = blue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(buttonSize)
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Start")
                    }

                    /**
                     * Test connection server button
                     */
                    Button(
                        onClick = { onConnectionClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(buttonSize)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Test")
                    }

                    /**
                     * Options button
                     */
                    Button(
                        onClick = { onOptionsClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(buttonSize)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Options")
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (isRound) 15.dp else 20.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val exitButtonSize = (maxWidthDp * 0.25f).dp

                /**
                 * Exit button
                 */
                Button(
                    onClick = {
                        exitProcess(0)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(exitButtonSize)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Exit")
                }
            }
        }
    }
}
