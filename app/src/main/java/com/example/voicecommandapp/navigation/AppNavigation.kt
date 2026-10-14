package com.example.voicecommandapp.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.voicecommandapp.screens.ConfirmationScreen
import com.example.voicecommandapp.screens.DashboardScreen
import com.example.voicecommandapp.screens.DeviceScreen
import com.example.voicecommandapp.screens.RoutineScreen
import com.example.voicecommandapp.screens.components.MicState
import com.example.voicecommandapp.screens.components.ScreenScaffold
import com.example.voicecommandapp.voice.RecognitionResult
import com.example.voicecommandapp.voice.VoiceRecognizer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SmartHomeApp() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = remember { SmartHomeState() }
    val recognizer = remember { VoiceRecognizer(context.applicationContext) }

    var micState by remember { mutableStateOf(MicState.IDLE) }
    var voiceMessage by remember { mutableStateOf("Tap the mic and say a command") }
    var showCommandPad by remember { mutableStateOf(false) }
    var modelReady by remember { mutableStateOf(false) }

    LaunchedEffect(recognizer) {
        recognizer.load()
        modelReady = recognizer.isReady
        recognizer.loadError?.let { voiceMessage = it }
    }

    DisposableEffect(recognizer) {
        onDispose { recognizer.close() }
    }

    fun listen() {
        if (micState != MicState.IDLE) return

        if (!modelReady) {
            voiceMessage = recognizer.loadError ?: "Model is still loading"
            return
        }

        scope.launch {
            micState = MicState.LISTENING
            try {
                val result = recognizer.recognize(onRecorded = { micState = MicState.PROCESSING })
                voiceMessage = when (result) {
                    is RecognitionResult.Command -> {
                        state.handle(result.command)
                        "Heard \"${result.command.label}\" (${percent(result.confidence)})"
                    }
                    is RecognitionResult.Unrecognized ->
                        "Not sure (\"${result.label}\" ${percent(result.confidence)}). Try again."
                    RecognitionResult.NoSpeech ->
                        "No speech detected. Try again closer to the mic."
                }
            } catch (e: Exception) {
                voiceMessage = e.message ?: "Voice recognition failed"
            } finally {
                micState = MicState.IDLE
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) listen() else voiceMessage = "Microphone permission is required"
    }

    val onMicClick = {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) listen() else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    // El botón atrás del sistema se comporta como el comando "no".
    BackHandler(enabled = state.currentScreen != AppScreen.DASHBOARD) {
        state.goBack()
    }

    val screen = state.currentScreen

    ScreenScaffold(
        screen = screen,
        title = when (screen) {
            AppScreen.DASHBOARD -> "Home Dashboard"
            AppScreen.DEVICE -> state.selectedDevice.title
            AppScreen.ROUTINES -> "Routines"
            AppScreen.CONFIRMATION -> "Confirm action"
        },
        statusLabel = when (screen) {
            AppScreen.DASHBOARD -> if (modelReady) "Voice Ready" else "Model not loaded"
            AppScreen.DEVICE -> "Device Control"
            AppScreen.ROUTINES -> "Routine Mode"
            AppScreen.CONFIRMATION -> "Confirmation"
        },
        confirmEnabled = state.pendingAction != null,
        onNavigate = state::navigateTo,
        micState = micState,
        voiceMessage = voiceMessage,
        feedback = state.feedback,
        showCommandPad = showCommandPad,
        onMicClick = onMicClick,
        onToggleCommandPad = { showCommandPad = !showCommandPad },
        onCommand = { command ->
            voiceMessage = "Test command \"${command.label}\""
            state.handle(command)
        }
    ) {

        when (screen) {

            AppScreen.DASHBOARD -> DashboardScreen(
                devices = state.devices,
                runningRoutine = state.runningRoutine,
                focusedIndex = state.dashboardFocus,
                onItemClick = state::openDashboardItem
            )

            AppScreen.DEVICE -> DeviceScreen(
                device = state.selectedDevice,
                focusedItem = state.deviceFocus,
                onItemClick = state::activateDeviceItem
            )

            AppScreen.ROUTINES -> RoutineScreen(
                routines = state.routines,
                focusedIndex = state.routineFocus,
                onRoutineClick = state::toggleRoutine
            )

            AppScreen.CONFIRMATION -> ConfirmationScreen(
                action = state.pendingAction,
                focusedChoice = state.confirmFocus,
                onConfirm = state::confirmPendingAction,
                onCancel = state::cancelPendingAction
            )
        }
    }
}

private fun percent(value: Float): String = "${(value * 100).roundToInt()}%"
