package com.smartbiostream.presentation.views.authenticationViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import com.smartbiostream.presentation.communication.CheckURL
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.management.UrlManager
import kotlinx.coroutines.launch

/**
 * View used to display the experiment ID input
 */
class ExperimentIDView {
    private val blue = Color(0xFF4A90E2) // Light blue

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ExperimentScreen(onSensorClick: () -> Unit, onBackClick: () -> Unit) {
        var id by remember { mutableStateOf("Experiment...") }
        val coroutineScope = rememberCoroutineScope() //coroutine scope used to check the reachability of the url
        var showErrorProtocol by remember { mutableStateOf(false) }
        var showErrorAvailability by remember { mutableStateOf(false) }


        /**
         * Variable used to control/display the keyboard
         */
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            androidx.wear.compose.material.Text(
                text = "Experiment ID",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Text field for the experiment ID
             */
            BasicTextField(
                value = id,
                onValueChange = { id = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, MaterialTheme.shapes.large)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                /**
                 * Back button
                 */
                Button(
                    onClick = {
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = blue,
                        contentColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                /**
                 * Next button
                 */
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val urls = CheckURL()
                            val measurements = urls.checkSendMeasurements()


                            // Server does not meet HTTPS and/or CA verification requirements
                            if (measurements == -1 && UrlManager.getRequestType() != "httpsuns") {
                                showErrorProtocol = true

                            // Server is not available
                            } else if (measurements == -2) {
                                showErrorAvailability = true

                            // Success
                            } else {
                                IdentificationManager.setID(id)
                                onSensorClick()
                            }

                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = blue,
                        contentColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }

        /**
         * Protocol error window
         */
        if (showErrorProtocol) {
            AlertDialog(
                onDismissRequest = { showErrorProtocol = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Text(
                            text = "Error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    androidx.compose.material.Text(text = "Wrong protocol configuration (HTTP/HTTPS and CA verification).")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorProtocol = false
                            onBackClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material.Text(text = "OK")
                    }
                }
            )
        }

        /**
         * Server not available error window
         */
        if (showErrorAvailability) {
            AlertDialog(
                onDismissRequest = { showErrorAvailability = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Text(
                            text = "Error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    androidx.compose.material.Text(text = "Server not available.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorAvailability = false
                            onBackClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material.Text(text = "OK")
                    }
                }
            )
        }
    }
}