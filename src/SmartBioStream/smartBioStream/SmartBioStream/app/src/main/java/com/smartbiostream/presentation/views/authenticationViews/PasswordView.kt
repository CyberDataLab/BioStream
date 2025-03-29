package com.smartbiostream.presentation.views.authenticationViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.wear.compose.material.OutlinedButton
import com.smartbiostream.presentation.communication.Login
import com.smartbiostream.presentation.communication.LoginCallback
import com.smartbiostream.presentation.management.IdentificationManager

/**
 * Class representing the view to choose the password.
 */
class PasswordView {

    private val blue = Color(0xFF4A90E2)      // Light blue

    /**
     * Function used to display the view
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun PasswordScreen(
        onLoginClick: () -> Unit,
        onErrorClick: () -> Unit,
        onErrorUrlClick: () -> Unit,
        onBackClick: () -> Unit
    ) {
        /**
         * Variables used to remember the stats of the password and errors
         */
        var password by remember { mutableStateOf("Password...") }
        var showErrorCredentials by remember { mutableStateOf(false) }
        var showErrorAvailability by remember { mutableStateOf(false) }
        var showErrorProtocol by remember { mutableStateOf(false) }
        var showErrorEndpoint by remember { mutableStateOf(false) }
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            androidx.wear.compose.material.Text(
                text = "Password",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Text field for the password
             */
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
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
                OutlinedButton(
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Spacer(modifier = Modifier.width(24.dp))

                /**
                 * Button to storage the password and change the view
                 */
                OutlinedButton(
                    onClick = {
                        IdentificationManager.setPassword(password)
                        val login = Login()
                        login.loginToServer(object : LoginCallback {
                            override fun onLoginSuccess() {
                                onLoginClick()
                            }

                            override fun onLoginErrorCredentials() {
                                showErrorCredentials = true
                            }

                            override fun onLoginErrorProtocol() {
                                showErrorProtocol = true
                            }

                            override fun onLoginErrorAvailability() {
                                showErrorAvailability = true
                            }

                            override fun onLoginErrorEndpoint() {
                                showErrorEndpoint = true
                            }

                        })
                    },
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

        /**
         * Invalid credentials error window
         */
        if (showErrorCredentials) {
            AlertDialog(
                onDismissRequest = { showErrorCredentials = false },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log in error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(text = "Invalid credentials. Please, try again later.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorCredentials = false
                            onErrorClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "OK")
                    }
                }
            )
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
                        Text(
                            text = "Error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(text = "Wrong protocol configuration (HTTP/HTTPS and CA verification)")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorProtocol = false
                            onErrorClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "OK")
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = "The server is not available. Please, try again later."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorAvailability = false
                            onErrorUrlClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }

        /**
         * Login endpoint not available error window
         */
        if (showErrorEndpoint) {
            AlertDialog(
                onDismissRequest = { showErrorEndpoint = false },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = "The login endpoint is not available. Please, try again later."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showErrorEndpoint = false
                            onErrorUrlClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}
