package com.smartbiostream.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun SmartBioStreamTheme(
    content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for the app.
     */
    MaterialTheme(
        content = content
    )
}