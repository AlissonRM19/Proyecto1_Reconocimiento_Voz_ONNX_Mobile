package com.example.voicecommandapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.voicecommandapp.screens.ConfirmationScreen
import com.example.voicecommandapp.screens.DeviceScreen
import com.example.voicecommandapp.screens.DashboardScreen
import com.example.voicecommandapp.screens.RoutineScreen

@Composable
fun SmartHomeApp() {

    var currentScreen by remember {
        mutableStateOf(AppScreen.DASHBOARD)
    }

    var selectedDevice by remember {
        mutableStateOf("Lights")
    }

    when (currentScreen) {

        AppScreen.DASHBOARD -> {
            DashboardScreen(
                onDeviceSelected = { device ->

                    selectedDevice = device

                    if (device == "Routines") {
                        currentScreen = AppScreen.ROUTINES
                    } else {
                        currentScreen = AppScreen.DEVICE
                    }
                }
            )
        }

        AppScreen.DEVICE -> {
            DeviceScreen(
                deviceName = selectedDevice,

                onBack = {
                    currentScreen = AppScreen.DASHBOARD
                },

                onRequestConfirmation = {
                    currentScreen = AppScreen.CONFIRMATION
                }
            )
        }

        AppScreen.ROUTINES -> {
            RoutineScreen(
                onBack = {
                    currentScreen = AppScreen.DASHBOARD
                }
            )
        }

        AppScreen.CONFIRMATION -> {
            ConfirmationScreen(
                onConfirm = {
                    currentScreen = AppScreen.DEVICE
                },

                onCancel = {
                    currentScreen = AppScreen.DEVICE
                }
            )
        }
    }
}