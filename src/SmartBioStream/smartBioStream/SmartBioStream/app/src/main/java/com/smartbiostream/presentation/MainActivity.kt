package com.smartbiostream.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.smartbiostream.presentation.management.IdentificationManager
import com.smartbiostream.presentation.management.UrlManager
import com.smartbiostream.presentation.views.TestConnectionView
import com.smartbiostream.presentation.views.authenticationViews.ExperimentIDView
import com.smartbiostream.presentation.views.measureViews.FirstPartSelectionView
import com.smartbiostream.presentation.views.optionsViews.OptionsIDView
import com.smartbiostream.presentation.views.MainScreenView
import com.smartbiostream.presentation.views.measureViews.MeasureView
import com.smartbiostream.presentation.views.optionsViews.OptionRequestView
import com.smartbiostream.presentation.views.optionsViews.OptionsPortView
import com.smartbiostream.presentation.views.optionsViews.OptionsIPView
import com.smartbiostream.presentation.views.authenticationViews.PasswordView
import com.smartbiostream.presentation.views.measureViews.SecondPartSelectionView
import com.smartbiostream.presentation.views.authenticationViews.UsernameView


/**
 * Main class representing the application's activity on Wear OS.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }

    }
}

/**
 * Enum class representing the different screens in the app.
 */
sealed class Screen {
    object Main : Screen()
    object TestConnection : Screen()
    object ExperimentID: Screen()
    object OptionsIP : Screen()
    object OptionsPort : Screen()
    object OptionsRequest : Screen()
    object OptionsID : Screen()
    object LoginUsername : Screen()
    object LoginPassword : Screen()
    object Selection : Screen()
    object SelectionPart2 : Screen()
    object Measurement : Screen()
}

/**
 * Main function that manages the application flow.
 */
@Composable
fun WearApp() {
    UrlManager.initialize(LocalContext.current)
    IdentificationManager.initialize(LocalContext.current)

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

    when (currentScreen) {
        is Screen.Main -> {
            MainScreenView().ThirdScreen(
                onAuthClick = { currentScreen = if (IdentificationManager.isIdentificationUsername()) Screen.LoginUsername else Screen.ExperimentID},
                onOptionsClick = { currentScreen = Screen.OptionsIP },
                onConnectionClick = { currentScreen = Screen.TestConnection }
            )
        }
        is Screen.TestConnection -> {
            TestConnectionView().ConnectionScreen(
                onBackClick = { currentScreen = Screen.Main }
            )
        }
        is Screen.ExperimentID -> {
            ExperimentIDView().ExperimentScreen(
                onSensorClick = { currentScreen = Screen.Selection},
                onBackClick = { currentScreen = Screen.Main },
            )
        }
        is Screen.OptionsIP -> {
            OptionsIPView().OptionsScreen(
                onBackClick = { currentScreen = Screen.Main },
                onPortClick = { currentScreen = Screen.OptionsPort }
            )
        }
        is Screen.OptionsPort -> {
            OptionsPortView().OptionsScreen(
                onBackClick = { currentScreen = Screen.OptionsIP },
                onRequestClick = { currentScreen = Screen.OptionsRequest }
            )
        }
        is Screen.OptionsRequest -> {
            OptionRequestView().OptionsScreen(
                onBackClick = { currentScreen = Screen.OptionsPort },
                onIdClick = { currentScreen = Screen.OptionsID }
            )
        }
        is Screen.OptionsID -> {
            OptionsIDView().IdView(
                onBackClick = { currentScreen = Screen.OptionsRequest },
                onMainClick = { currentScreen = Screen.Main }
            )
        }
        is Screen.LoginUsername -> {
            if (IdentificationManager.isIdentificationUsername()) {
                UsernameView().UsernameScreen(
                    onLoginClick = { currentScreen = Screen.LoginPassword },
                    onBackClick = { currentScreen = Screen.Main }
                )
            } else if (IdentificationManager.isIdentificationID()) {
                ExperimentIDView().ExperimentScreen(
                    onSensorClick = { currentScreen = Screen.Selection },
                    onBackClick = { currentScreen = Screen.Main }
                )
            }
        }
        is Screen.LoginPassword -> {
            PasswordView().PasswordScreen(
                onLoginClick = { currentScreen = Screen.Selection },
                onErrorClick = { currentScreen = Screen.LoginUsername },
                onErrorUrlClick = { currentScreen = Screen.Main },
                onBackClick = { currentScreen = Screen.LoginUsername }
            )
        }
        is Screen.Selection -> {
            FirstPartSelectionView().SelectionScreen(
                onNextClick = { currentScreen = Screen.SelectionPart2 }
            )
        }
        is Screen.SelectionPart2 -> {
            SecondPartSelectionView().SelectionScreen2(
                onHRClick = { currentScreen = Screen.Measurement },
                onBackClick = { currentScreen = Screen.Selection }
            )
        }
        is Screen.Measurement -> {
            MeasureView().MeasureScreen(
                context = LocalContext.current,
                onLoginClick = {
                    currentScreen = Screen.Main
                },
                onSensorClick = {
                    currentScreen = Screen.Selection
                }
            )
        }
    }
}


/**
 * Preview of the user interface on a small, round Wear OS device.
 */
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}