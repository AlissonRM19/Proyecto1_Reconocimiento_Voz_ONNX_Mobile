package com.example.voicecommandapp.navigation

enum class AppScreen(
    val tabLabel: String,
    val number: Int,
    val listeningHint: String,
    val voiceHelp: String
) {
    DASHBOARD(
        tabLabel = "Dashboard",
        number = 1,
        listeningHint = "up / down / left / right / yes",
        voiceHelp = "up/down/left/right move the focus, yes opens the selected card, on/off switch the focused device."
    ),
    DEVICE(
        tabLabel = "Device",
        number = 2,
        listeningHint = "on / off / yes / no",
        voiceHelp = "on/off control the device, up/down/left/right move the focus, yes runs the highlighted action, no returns."
    ),
    ROUTINES(
        tabLabel = "Routine",
        number = 3,
        listeningHint = "go / stop / up / down",
        voiceHelp = "up/down move between routines, go starts the selected one, stop interrupts it, no returns."
    ),
    CONFIRMATION(
        tabLabel = "Confirm",
        number = 4,
        listeningHint = "yes / no",
        voiceHelp = "Confirmation screens reduce ambiguity: say yes to execute or no to cancel and go back."
    )
}
