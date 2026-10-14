package com.example.voicecommandapp.voice

import android.content.Context
import android.util.Log
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

sealed interface RecognitionResult {

    data class Command(
        val command: VoiceCommand,
        val confidence: Float
    ) : RecognitionResult

    // El modelo no está seguro, o predijo una clase que no es comando (p. ej. _unknown_).
    data class Unrecognized(
        val label: String,
        val confidence: Float
    ) : RecognitionResult

    data object NoSpeech : RecognitionResult
}

/** Grabación → espectrograma log-mel → ONNX → comando. Todo local en el dispositivo. */
class VoiceRecognizer(private val context: Context) : AutoCloseable {

    private val recorder = AudioRecorder()
    private val melSpectrogram = MelSpectrogram()
    private var classifier: CommandClassifier? = null

    var loadError: String? = null
        private set

    val isReady: Boolean
        get() = classifier != null

    suspend fun load() {
        if (classifier != null) return
        withContext(Dispatchers.IO) {
            try {
                classifier = CommandClassifier.load(context)
                loadError = null
            } catch (e: Exception) {
                loadError = e.message ?: "Could not load the model"
            }
        }
    }

    /** [onRecorded] se llama al terminar de grabar, antes de procesar. */
    suspend fun recognize(onRecorded: () -> Unit = {}): RecognitionResult {
        val model = checkNotNull(classifier) { loadError ?: "Model not loaded" }

        val recording = recorder.record()
        onRecorded()
        saveDebugRecording(recording)

        return withContext(Dispatchers.Default) {
            val window = AudioRecorder.loudestWindow(recording)
            val rms = AudioRecorder.rms(window)
            Log.d(TAG, "recording: rms=$rms peak=${window.maxOf { abs(it) }}")
            if (rms < AudioConfig.SILENCE_RMS) {
                return@withContext RecognitionResult.NoSpeech
            }

            val clip = AudioRecorder.boostQuiet(window)
            val features = melSpectrogram.compute(clip)
            val frames = melSpectrogram.numFrames(clip.size)
            val probabilities = model.classify(features, melSpectrogram.nMels, frames)

            val best = probabilities.indices.maxBy { probabilities[it] }
            val label = model.labels[best]
            val confidence = probabilities[best]
            val command = VoiceCommand.fromLabel(label)
            Log.d(TAG, "prediction: $label ${"%.2f".format(confidence)}")

            if (command != null && confidence >= AudioConfig.MIN_CONFIDENCE) {
                RecognitionResult.Command(command, confidence)
            } else {
                RecognitionResult.Unrecognized(label, confidence)
            }
        }
    }

    // Guarda las últimas grabaciones en cache/recordings para analizarlas
    // (adb exec-out run-as com.example.voicecommandapp ...).
    private fun saveDebugRecording(samples: FloatArray) {
        try {
            val dir = File(context.cacheDir, "recordings").apply { mkdirs() }
            dir.listFiles()?.sortedBy { it.name }?.dropLast(MAX_DEBUG_RECORDINGS - 1)?.forEach { it.delete() }
            AudioRecorder.writeWav(File(dir, "rec_${System.currentTimeMillis()}.wav"), samples)
        } catch (e: Exception) {
            Log.w(TAG, "Could not save debug recording", e)
        }
    }

    override fun close() {
        classifier?.close()
        classifier = null
    }

    private companion object {
        const val TAG = "VoiceRecognizer"
        const val MAX_DEBUG_RECORDINGS = 20
    }
}
