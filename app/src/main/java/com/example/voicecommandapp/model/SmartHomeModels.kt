package com.example.voicecommandapp.model

data class SmartDevice(
    val name: String,
    val title: String,
    val subtitle: String,
    val isOn: Boolean
)

data class Routine(
    val title: String,
    val subtitle: String,
    // Estado en que deja cada dispositivo (por nombre) al iniciarse.
    val effects: Map<String, Boolean>,
    val isRunning: Boolean = false
)

/** Acciones sensibles que pasan por la pantalla de confirmación. */
enum class ConfirmableAction(
    val title: String,
    val description: String
) {
    TURN_OFF_ALL(
        title = "Turn off all devices?",
        description = "Lights, fan and TV will be switched off and any running routine will stop. Say yes to accept or no to cancel and return."
    )
}
