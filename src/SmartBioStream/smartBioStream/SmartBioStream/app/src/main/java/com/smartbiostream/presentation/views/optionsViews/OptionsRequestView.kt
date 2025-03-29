package com.smartbiostream.presentation.views.optionsViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.smartbiostream.presentation.management.UrlManager

/**
 * View used to display the server request
 */
class OptionRequestView {
    private val blue = Color(0xFF4A90E2) // Light blue
    private val darkBlue = Color(0xFF1C3A5A) // Dark blue

    /**
     * Function used to display the view
     */
    @Composable
    fun OptionsScreen(onBackClick: () -> Unit, onIdClick: () -> Unit) {
        /**
         * Variables used to remember the stats of the toggle buttons
         */
        var isHttps by remember { mutableStateOf(UrlManager.getRequestType() == "https" || UrlManager.getRequestType() == "httpsuns") }
        var isCaAuth by remember { mutableStateOf(UrlManager.getRequestType() == "https") }

        val density = LocalDensity.current

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            /**
             * Variables used to know if the smartwatch is round or not
             */
            val maxWidthDp = with(density) { constraints.maxWidth.toDp() }.value
            val maxHeightDp = with(density) { constraints.maxHeight.toDp() }.value
            val isRound = maxWidthDp == maxHeightDp

            val paddingSize = if (isRound) 12.dp else 16.dp
            val textSize = if (isRound) 14.sp else 16.sp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingSize),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Protocol",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Use HTTPS",
                        fontSize = textSize,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    /**
                     * Switch HTTP/HTTPS button
                     */
                    Switch(
                        checked = isHttps,
                        onCheckedChange = { toggled ->
                            if (!toggled && isCaAuth) isCaAuth = false
                            isHttps = toggled
                            val response =
                                if (toggled && isCaAuth)
                                                "https"
                                else if (toggled && !isCaAuth)
                                                "httpsuns"
                                else
                                    "http"
                            UrlManager.setRequestType(response)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = blue,
                            uncheckedThumbColor = darkBlue,
                            checkedTrackColor = darkBlue.copy(alpha = 0.6f),
                            uncheckedTrackColor = blue.copy(alpha = 0.6f)
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CA verification",
                        fontSize = textSize,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    /**
                     * Switch certificate authorization button
                     */
                    Switch(
                        checked = isCaAuth,
                        onCheckedChange = { toggled ->
                            isCaAuth = toggled
                            if (toggled) {
                                isHttps = true
                                UrlManager.setRequestType("https")
                            } else {
                                UrlManager.setRequestType(if (isHttps) "httpsuns" else "http")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = blue,
                            uncheckedThumbColor = darkBlue,
                            checkedTrackColor = darkBlue.copy(alpha = 0.6f),
                            uncheckedTrackColor = blue.copy(alpha = 0.6f)
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
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
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
                    OutlinedButton(
                        onClick = {
                            if (!isHttps) UrlManager.setRequestType("http")
                            onIdClick()
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        shape = CircleShape,
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
        }
    }
}
