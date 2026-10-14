package com.example.voicecommandapp.voice

/**
 * Los 10 comandos autorizados (Tabla I del enunciado).
 * [label] es el nombre de la clase tal como aparece en Speech Commands v0.02.
 */
enum class VoiceCommand(val label: String) {
    YES("yes"),
    NO("no"),
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right"),
    ON("on"),
    OFF("off"),
    STOP("stop"),
    GO("go");

    companion object {

        fun fromLabel(label: String): VoiceCommand? {
            val normalized = label.trim().lowercase()
            return entries.firstOrNull { it.label == normalized }
        }
    }
}
