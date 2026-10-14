package com.example.voicecommandapp

import com.example.voicecommandapp.voice.MelSpectrogram
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Verifica que el preprocesamiento de la app produce el mismo espectrograma
 * que librosa. mel_reference.txt se generó con librosa usando los
 * parámetros documentados en AudioConfig sobre la misma señal sintética.
 */
class MelSpectrogramTest {

    @Test
    fun matchesTorchaudioReference() {
        val signal = FloatArray(16_000) { n ->
            val t = n / 16_000.0
            (0.5 * sin(2 * PI * 440 * t) +
                0.3 * sin(2 * PI * 1234 * t) +
                0.1 * sin(2 * PI * (200 + 1500 * t) * t)).toFloat()
        }

        val lines = javaClass.classLoader!!
            .getResourceAsStream("mel_reference.txt")!!
            .bufferedReader()
            .readLines()
        val (nMels, frames) = lines.first().split(" ").map { it.toInt() }
        val expected = lines.drop(1).flatMap { row -> row.split(" ").map { it.toFloat() } }

        val melSpectrogram = MelSpectrogram()
        val actual = melSpectrogram.compute(signal)

        assertEquals(nMels, melSpectrogram.nMels)
        assertEquals(frames, melSpectrogram.numFrames(signal.size))
        assertEquals(expected.size, actual.size)

        val maxError = expected.indices.maxOf { abs(expected[it] - actual[it]) }
        println("Max difference vs librosa: $maxError dB")
        assertTrue("Max difference vs librosa: $maxError dB", maxError < 0.05f)
    }
}
