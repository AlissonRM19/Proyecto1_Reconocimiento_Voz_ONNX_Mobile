package com.example.voicecommandapp.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.voicecommandapp.model.ConfirmableAction
import com.example.voicecommandapp.model.Routine
import com.example.voicecommandapp.model.SmartDevice
import com.example.voicecommandapp.voice.VoiceCommand

/**
 * Estado de toda la app y traducción de cada comando de voz a una acción
 * según la pantalla actual. Los toques en pantalla usan las mismas funciones.
 */
class SmartHomeState {

    var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
        private set

    val devices = mutableStateListOf(
        SmartDevice("Lights", "Living Room Lights", "Living room and\nbedroom lights", isOn = true),
        SmartDevice("Fan", "Fan", "Air circulation\nand comfort mode", isOn = false),
        SmartDevice("TV", "TV", "Media and\nambient screen", isOn = false)
    )

    val routines = mutableStateListOf(
        Routine(
            title = "Study Mode",
            subtitle = "Desk light on, fan low,\nnotifications muted.",
            effects = mapOf("Lights" to true, "Fan" to true, "TV" to false)
        ),
        Routine(
            title = "Night Mode",
            subtitle = "Warm lights, TV off, reduced\nbrightness.",
            effects = mapOf("Lights" to true, "Fan" to false, "TV" to false)
        ),
        Routine(
            title = "Welcome Home",
            subtitle = "Lights on and ambient music\nprepared.",
            effects = mapOf("Lights" to true, "TV" to true)
        )
    )

    // Foco visual de cada pantalla (lo que mueven up/down/left/right).
    var dashboardFocus by mutableIntStateOf(0)
        private set
    var deviceFocus by mutableIntStateOf(DEVICE_TURN_ON)
        private set
    var routineFocus by mutableIntStateOf(0)
        private set
    var confirmFocus by mutableIntStateOf(CONFIRM_YES)
        private set

    var selectedDeviceIndex by mutableIntStateOf(0)
        private set

    var pendingAction by mutableStateOf<ConfirmableAction?>(null)
        private set

    // Resultado de la última acción, se muestra en el panel de voz.
    var feedback by mutableStateOf("")
        private set

    private var screenBeforeConfirmation = AppScreen.DASHBOARD

    val selectedDevice: SmartDevice
        get() = devices[selectedDeviceIndex]

    val runningRoutine: Routine?
        get() = routines.firstOrNull { it.isRunning }

    // Tarjetas del dashboard: un dispositivo por tarjeta y al final "Routines".
    val dashboardItemCount: Int
        get() = devices.size + 1

    fun handle(command: VoiceCommand) {
        val handled = when (currentScreen) {
            AppScreen.DASHBOARD -> handleDashboard(command)
            AppScreen.DEVICE -> handleDevice(command)
            AppScreen.ROUTINES -> handleRoutines(command)
            AppScreen.CONFIRMATION -> handleConfirmation(command)
        }

        if (!handled) {
            feedback = "\"${command.label}\" is not used here. Try: ${currentScreen.listeningHint}"
        }
    }

    // ---------- Navegación ----------

    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.CONFIRMATION && pendingAction == null) return
        if (screen == currentScreen) return
        currentScreen = screen
        feedback = "${screen.tabLabel} screen"
    }

    /** Equivale al comando "no" / botón atrás. Devuelve false si no hay a dónde volver. */
    fun goBack(): Boolean = when (currentScreen) {
        AppScreen.DASHBOARD -> false
        AppScreen.CONFIRMATION -> {
            cancelPendingAction()
            true
        }
        else -> {
            navigateTo(AppScreen.DASHBOARD)
            true
        }
    }

    fun openDashboardItem(index: Int) {
        dashboardFocus = index
        if (index < devices.size) {
            selectedDeviceIndex = index
            deviceFocus = if (devices[index].isOn) DEVICE_TURN_ON else DEVICE_TURN_OFF
            navigateTo(AppScreen.DEVICE)
            feedback = "Opened ${devices[index].name}"
        } else {
            navigateTo(AppScreen.ROUTINES)
            feedback = "Opened Routines"
        }
    }

    // ---------- Dispositivos ----------

    fun setDevice(index: Int, on: Boolean) {
        devices[index] = devices[index].copy(isOn = on)
        feedback = "${devices[index].name} turned ${if (on) "on" else "off"}"
    }

    fun setSelectedDevice(on: Boolean) {
        deviceFocus = if (on) DEVICE_TURN_ON else DEVICE_TURN_OFF
        setDevice(selectedDeviceIndex, on)
    }

    fun activateDeviceItem(item: Int) {
        deviceFocus = item
        when (item) {
            DEVICE_TURN_ON -> setSelectedDevice(true)
            DEVICE_TURN_OFF -> setSelectedDevice(false)
            DEVICE_ALL_OFF -> requestConfirmation(ConfirmableAction.TURN_OFF_ALL)
            DEVICE_BACK -> navigateTo(AppScreen.DASHBOARD)
        }
    }

    // ---------- Rutinas ----------

    fun startRoutine(index: Int) {
        routineFocus = index
        val routine = routines[index]

        for (i in routines.indices) {
            routines[i] = routines[i].copy(isRunning = i == index)
        }
        for (i in devices.indices) {
            routine.effects[devices[i].name]?.let { on ->
                devices[i] = devices[i].copy(isOn = on)
            }
        }
        feedback = "${routine.title} started"
    }

    fun stopRoutine(index: Int) {
        routineFocus = index
        val routine = routines[index]
        if (!routine.isRunning) {
            feedback = "${routine.title} is not running"
            return
        }
        routines[index] = routine.copy(isRunning = false)
        feedback = "${routine.title} stopped"
    }

    fun toggleRoutine(index: Int) {
        if (routines[index].isRunning) stopRoutine(index) else startRoutine(index)
    }

    // ---------- Confirmación ----------

    fun requestConfirmation(action: ConfirmableAction) {
        pendingAction = action
        confirmFocus = CONFIRM_YES
        screenBeforeConfirmation = currentScreen
        navigateTo(AppScreen.CONFIRMATION)
        feedback = "Waiting for confirmation"
    }

    fun confirmPendingAction() {
        val action = pendingAction ?: return
        confirmFocus = CONFIRM_YES
        when (action) {
            ConfirmableAction.TURN_OFF_ALL -> {
                for (i in devices.indices) devices[i] = devices[i].copy(isOn = false)
                for (i in routines.indices) routines[i] = routines[i].copy(isRunning = false)
                deviceFocus = DEVICE_TURN_OFF
            }
        }
        pendingAction = null
        currentScreen = screenBeforeConfirmation
        feedback = "Done: all devices are off"
    }

    fun cancelPendingAction() {
        confirmFocus = CONFIRM_NO
        pendingAction = null
        currentScreen = screenBeforeConfirmation
        feedback = "Action cancelled"
    }

    // ---------- Comandos por pantalla ----------

    private fun handleDashboard(command: VoiceCommand): Boolean {
        when (command) {
            VoiceCommand.UP, VoiceCommand.DOWN, VoiceCommand.LEFT, VoiceCommand.RIGHT -> {
                dashboardFocus = moveInGrid(dashboardFocus, command, columns = 2, count = dashboardItemCount)
                feedback = "Focus: ${dashboardItemTitle(dashboardFocus)}"
            }
            VoiceCommand.YES, VoiceCommand.GO -> openDashboardItem(dashboardFocus)
            VoiceCommand.ON, VoiceCommand.OFF -> {
                if (dashboardFocus >= devices.size) return false
                setDevice(dashboardFocus, command == VoiceCommand.ON)
            }
            else -> return false
        }
        return true
    }

    private fun handleDevice(command: VoiceCommand): Boolean {
        when (command) {
            VoiceCommand.ON -> setSelectedDevice(true)
            VoiceCommand.OFF -> setSelectedDevice(false)
            VoiceCommand.YES -> activateDeviceItem(deviceFocus)
            VoiceCommand.NO -> goBack()
            VoiceCommand.LEFT, VoiceCommand.RIGHT -> {
                // Solo la primera fila (Turn On / Turn Off) tiene dos columnas.
                if (deviceFocus > DEVICE_TURN_OFF) return false
                deviceFocus = if (command == VoiceCommand.LEFT) DEVICE_TURN_ON else DEVICE_TURN_OFF
                feedback = "Focus: ${deviceItemTitle(deviceFocus)}"
            }
            VoiceCommand.UP -> {
                deviceFocus = when (deviceFocus) {
                    DEVICE_BACK -> DEVICE_ALL_OFF
                    DEVICE_ALL_OFF -> DEVICE_TURN_ON
                    else -> deviceFocus
                }
                feedback = "Focus: ${deviceItemTitle(deviceFocus)}"
            }
            VoiceCommand.DOWN -> {
                deviceFocus = when (deviceFocus) {
                    DEVICE_TURN_ON, DEVICE_TURN_OFF -> DEVICE_ALL_OFF
                    else -> DEVICE_BACK
                }
                feedback = "Focus: ${deviceItemTitle(deviceFocus)}"
            }
            else -> return false
        }
        return true
    }

    private fun handleRoutines(command: VoiceCommand): Boolean {
        when (command) {
            VoiceCommand.UP -> {
                routineFocus = (routineFocus - 1).coerceAtLeast(0)
                feedback = "Focus: ${routines[routineFocus].title}"
            }
            VoiceCommand.DOWN -> {
                routineFocus = (routineFocus + 1).coerceAtMost(routines.lastIndex)
                feedback = "Focus: ${routines[routineFocus].title}"
            }
            VoiceCommand.GO -> startRoutine(routineFocus)
            VoiceCommand.STOP -> {
                // Si la rutina enfocada no corre, detiene la que esté activa.
                val running = routines.indexOfFirst { it.isRunning }
                when {
                    routines[routineFocus].isRunning -> stopRoutine(routineFocus)
                    running >= 0 -> stopRoutine(running)
                    else -> feedback = "No routine is running"
                }
            }
            VoiceCommand.NO -> goBack()
            else -> return false
        }
        return true
    }

    private fun handleConfirmation(command: VoiceCommand): Boolean {
        when (command) {
            VoiceCommand.YES -> confirmPendingAction()
            VoiceCommand.NO -> cancelPendingAction()
            VoiceCommand.LEFT -> confirmFocus = CONFIRM_YES
            VoiceCommand.RIGHT -> confirmFocus = CONFIRM_NO
            else -> return false
        }
        return true
    }

    private fun moveInGrid(index: Int, command: VoiceCommand, columns: Int, count: Int): Int {
        val target = when (command) {
            VoiceCommand.UP -> index - columns
            VoiceCommand.DOWN -> index + columns
            VoiceCommand.LEFT -> if (index % columns == 0) index else index - 1
            VoiceCommand.RIGHT -> if (index % columns == columns - 1) index else index + 1
            else -> index
        }
        return if (target in 0 until count) target else index
    }

    private fun dashboardItemTitle(index: Int): String =
        if (index < devices.size) devices[index].name else "Routines"

    private fun deviceItemTitle(item: Int): String = when (item) {
        DEVICE_TURN_ON -> "Turn On"
        DEVICE_TURN_OFF -> "Turn Off"
        DEVICE_ALL_OFF -> "All devices off"
        else -> "Back to dashboard"
    }

    companion object {
        const val DEVICE_TURN_ON = 0
        const val DEVICE_TURN_OFF = 1
        const val DEVICE_ALL_OFF = 2
        const val DEVICE_BACK = 3

        const val CONFIRM_YES = 0
        const val CONFIRM_NO = 1
    }
}
