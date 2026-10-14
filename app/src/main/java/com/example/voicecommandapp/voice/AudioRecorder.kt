package com.example.voicecommandapp.voice

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/** Graba audio PCM 16 bits, mono, a [AudioConfig.SAMPLE_RATE] (mismo formato que el dataset). */
class AudioRecorder {

    // Quien llama debe verificar el permiso RECORD_AUDIO antes.
    @SuppressLint("MissingPermission")
    suspend fun record(numSamples: Int = AudioConfig.RECORD_SAMPLES): FloatArray =
        withContext(Dispatchers.IO) {

            val minBuffer = AudioRecord.getMinBufferSize(
                AudioConfig.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                AudioConfig.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                max(minBuffer, numSamples * 2)
            )

            check(recorder.state == AudioRecord.STATE_INITIALIZED) {
                "Could not open the microphone"
            }

            val pcm = ShortArray(numSamples)
            try {
                recorder.startRecording()
                var read = 0
                while (read < numSamples) {
                    ensureActive()
                    val count = recorder.read(pcm, read, numSamples - read)
                    check(count >= 0) { "Microphone read error ($count)" }
                    read += count
                }
            } finally {
                recorder.stop()
                recorder.release()
            }

            // Igual que torchaudio.load: enteros de 16 bits escalados a [-1, 1].
            FloatArray(numSamples) { pcm[it] / 32768f }
        }

    companion object {

        /** Ventana de [windowSize] muestras con mayor energía, buscada en pasos de 10 ms. */
        fun loudestWindow(
            samples: FloatArray,
            windowSize: Int = AudioConfig.CLIP_SAMPLES,
            step: Int = AudioConfig.SAMPLE_RATE / 100
        ): FloatArray {
            if (samples.size <= windowSize) return samples.copyOf(windowSize)

            val prefix = DoubleArray(samples.size + 1)
            for (i in samples.indices) {
                prefix[i + 1] = prefix[i] + samples[i] * samples[i]
            }

            var bestStart = 0
            var bestEnergy = -1.0
            var start = 0
            while (start + windowSize <= samples.size) {
                val energy = prefix[start + windowSize] - prefix[start]
                if (energy > bestEnergy) {
                    bestEnergy = energy
                    bestStart = start
                }
                start += step
            }

            return samples.copyOfRange(bestStart, bestStart + windowSize)
        }

        /** Amplifica [samples] si su pico está por debajo de [targetPeak]. */
        fun boostQuiet(
            samples: FloatArray,
            targetPeak: Float = AudioConfig.TARGET_PEAK,
            maxGain: Float = AudioConfig.MAX_GAIN
        ): FloatArray {
            val peak = samples.maxOf { abs(it) }
            if (peak <= 0f || peak >= targetPeak) return samples
            val gain = minOf(targetPeak / peak, maxGain)
            return FloatArray(samples.size) { samples[it] * gain }
        }

        /** WAV PCM 16 bits mono a [AudioConfig.SAMPLE_RATE]. */
        fun writeWav(file: File, samples: FloatArray) {
            val dataSize = samples.size * 2
            val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
            buffer.put("RIFF".toByteArray()).putInt(36 + dataSize).put("WAVE".toByteArray())
            buffer.put("fmt ".toByteArray()).putInt(16).putShort(1).putShort(1)
                .putInt(AudioConfig.SAMPLE_RATE).putInt(AudioConfig.SAMPLE_RATE * 2)
                .putShort(2).putShort(16)
            buffer.put("data".toByteArray()).putInt(dataSize)
            for (sample in samples) {
                buffer.putShort((sample * 32767f).coerceIn(-32768f, 32767f).toInt().toShort())
            }
            file.writeBytes(buffer.array())
        }

        fun rms(samples: FloatArray): Float {
            if (samples.isEmpty()) return 0f
            val sum = samples.fold(0.0) { acc, s -> acc + s * s }
            return sqrt(sum / samples.size).toFloat()
        }
    }
}
