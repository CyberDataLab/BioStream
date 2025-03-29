package com.smartbiostream.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import com.smartbiostream.presentation.communication.CheckURL
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.management.UrlManager
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * User view to test the server connection
 */
class TestConnectionView {

    /**
     * Function used to show the view
     */
    @Composable
    fun ConnectionScreen(onBackClick: () -> Unit) {
        val blue = Color(0xFF4A90E2)     // Light blue
        val coroutineScope = rememberCoroutineScope()

        /**
         * Variables used to show the error/success windows
         */
        var showErrorProtocol by remember { mutableStateOf(false) }
        var showErrorAvailability by remember { mutableStateOf(false) }
        var showErrorAuthentication by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Text(
                text = "Test server\nconnection",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            /**
             * Button to test the server connection
             */
            Button(
                onClick = {
                    coroutineScope.launch {
                        val urls = CheckURL()
                        val login = urls.checkLogin()
                        val measurements = urls.checkSendMeasurements()
                        val logout = urls.checkLogout()
                        // Server does not meet HTTPS and/or CA verification requirements
                        if (measurements == -1 && UrlManager.getRequestType() != "httpsuns") {
                            showErrorProtocol = true

                        // Server is not available
                        } else if (measurements == -2) {
                            showErrorAvailability = true

                        // If username and password authentication is required
                        } else if (IdentificationManager.isIdentificationUsername()) {
                            /* Error in login or logout endpoints
                               but the measures endpoint is available.
                               Code 400 is not considered an error because the tests are not
                               sending real data.
                               */
                            if (!(login in 200..399) || !(logout in 200..399)) {
                                showErrorAuthentication = true
                            } else {
                                showSuccessDialog = true
                            }
                        } else {
                            showSuccessDialog = true
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
            ) {
                Text(text = "Start test", fontSize = 16.sp)
            }


            Spacer(modifier = Modifier.height(4.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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

        /**
         * Login or logout endpoints not available
         */
        if (showErrorAuthentication) {
            AlertDialog(
                onDismissRequest = { showErrorAuthentication = false },
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
                    androidx.compose.material.Text(text = "Server does not provide login and logout endpoints.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorAuthentication = false
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
         * Success window
         */
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Text(
                            text = "Connection success",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    androidx.compose.material.Text(text = "Server is available.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
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
