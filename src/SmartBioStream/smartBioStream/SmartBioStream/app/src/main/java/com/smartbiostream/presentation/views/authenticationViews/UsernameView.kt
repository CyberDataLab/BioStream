package com.smartbiostream.presentation.views.authenticationViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.wear.compose.material.OutlinedButton
import com.smartbiostream.presentation.management.IdentificationManager

/**
 * Class representing the view to set the username.
 */
class UsernameView {

    private val blue = Color(0xFF4A90E2)      // Light blue

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun UsernameScreen(onLoginClick: () -> Unit, onBackClick: () -> Unit) {

        /**
         * Variable used to remember the stats of the username
         */
        var username by remember { mutableStateOf("Username...") }
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            androidx.wear.compose.material.Text(
                text = "Username",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Username input
             */
            BasicTextField(
                value = username,
                onValueChange = { username = it },
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(24.dp))

                /**
                 * Button used to storage the username and change the view
                 */
                OutlinedButton(
                    onClick = {
                        IdentificationManager.setUsername(username)
                        onLoginClick()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = blue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
