package com.smartbiostream.presentation.views.optionsViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.smartbiostream.presentation.management.IdentificationManager

/**
 * View used to display the different ways to authenticate in the platform
 */
class OptionsIDView {

    @Composable
    fun IdView(onBackClick: () -> Unit, onMainClick: () -> Unit) {
        val blue = Color(0xFF4A90E2)     // Light blue activated
        val darkBlue = Color(0xFF1C3A5A) // Dark blue deactivated


        /**
         * Variable used to switch into "User/password" and "ID"
         */
        var isUserSelected by remember { mutableStateOf(IdentificationManager.isIdentificationUsername()) }

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
                    text = "Identification",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isUserSelected) "User/password" else "ID",
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    /**
                     * Switch button to change into User/password and ID
                     */
                    Switch(
                        checked = isUserSelected,
                        onCheckedChange = { selected ->
                            isUserSelected = selected
                            IdentificationManager.setUsernameIdentification(selected)
                            IdentificationManager.setIDIdentification(!selected)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = blue,
                            uncheckedThumbColor = darkBlue,
                            checkedTrackColor = blue.copy(alpha = 0.6f),
                            uncheckedTrackColor = darkBlue.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
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
                            onClick = onBackClick,
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        /**
                         * Next button
                         */
                        Button(
                            onClick = onMainClick,
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(backgroundColor = blue, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        }
                    }
                }
            }
        }
    }
}
