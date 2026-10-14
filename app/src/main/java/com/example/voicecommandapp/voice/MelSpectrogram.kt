package com.example.voicecommandapp.voice

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Reimplementación de librosa.feature.melspectrogram + power_to_db (o su
 * equivalente en torchaudio) con los parámetros de [AudioConfig], para que la
 * app genere exactamente la misma "imagen" que se usó para entrenar.
 */
class MelSpectrogram(
    private val sampleRate: Int = AudioConfig.SAMPLE_RATE,
    private val nFft: Int = AudioConfig.N_FFT,
    private val winLength: Int = AudioConfig.WIN_LENGTH,
    private val hopLength: Int = AudioConfig.HOP_LENGTH,
    val nMels: Int = AudioConfig.N_MELS,
    private val fMin: Double = AudioConfig.F_MIN,
    private val fMax: Double = AudioConfig.F_MAX,
    private val topDb: Double = AudioConfig.TOP_DB,
    private val padMode: AudioConfig.PadMode = AudioConfig.PAD_MODE,
    private val melScale: AudioConfig.MelScale = AudioConfig.MEL_SCALE,
    private val melNorm: AudioConfig.MelNorm = AudioConfig.MEL_NORM,
    private val dbReference: AudioConfig.DbReference = AudioConfig.DB_REFERENCE,
    private val normalization: AudioConfig.Normalization = AudioConfig.NORMALIZATION
) {

    private val nFreqs = nFft / 2 + 1
    private val window = paddedHannWindow()
    private val melFilters = melFilterBank()

    init {
        require(nFft > 0 && nFft and (nFft - 1) == 0) { "n_fft debe ser potencia de 2" }
        require(winLength <= nFft) { "win_length no puede ser mayor que n_fft" }
    }

    // Con center=True se agregan n_fft/2 muestras a cada lado.
    fun numFrames(numSamples: Int): Int = 1 + numSamples / hopLength

    /** Devuelve el espectrograma log-mel en orden [mel][frame], aplanado. */
    fun compute(samples: FloatArray): FloatArray {
        val pad = nFft / 2
        val frames = numFrames(samples.size)
        val output = FloatArray(nMels * frames)

        val real = DoubleArray(nFft)
        val imag = DoubleArray(nFft)
        val power = DoubleArray(nFreqs)

        for (frame in 0 until frames) {
            val start = frame * hopLength - pad
            for (i in 0 until nFft) {
                real[i] = sampleAt(samples, start + i) * window[i]
                imag[i] = 0.0
            }

            fft(real, imag)

            for (f in 0 until nFreqs) {
                power[f] = real[f] * real[f] + imag[f] * imag[f]
            }

            for (m in 0 until nMels) {
                val filter = melFilters[m]
                var energy = 0.0
                for (f in 0 until nFreqs) {
                    energy += filter[f] * power[f]
                }
                output[m * frames + frame] = energy.toFloat()
            }
        }

        toDecibels(output)
        normalize(output)
        return output
    }

    private fun sampleAt(samples: FloatArray, index: Int): Double {
        if (index in samples.indices) return samples[index].toDouble()
        return when (padMode) {
            AudioConfig.PadMode.CONSTANT -> 0.0
            AudioConfig.PadMode.REFLECT -> {
                val reflected = if (index < 0) -index else 2 * (samples.size - 1) - index
                samples[reflected].toDouble()
            }
        }
    }

    // Ventana Hann periódica (librosa y torch), centrada dentro de n_fft si win_length < n_fft.
    private fun paddedHannWindow(): DoubleArray {
        val result = DoubleArray(nFft)
        val offset = (nFft - winLength) / 2
        for (i in 0 until winLength) {
            result[offset + i] = 0.5 - 0.5 * cos(2.0 * PI * i / winLength)
        }
        return result
    }

    // Igual que librosa.filters.mel / torchaudio.functional.melscale_fbanks.
    private fun melFilterBank(): Array<DoubleArray> {
        val allFreqs = DoubleArray(nFreqs) { it * (sampleRate / 2.0) / (nFreqs - 1) }
        val melMin = hzToMel(fMin)
        val melMax = hzToMel(fMax)
        val hzPoints = DoubleArray(nMels + 2) {
            melToHz(melMin + it * (melMax - melMin) / (nMels + 1))
        }

        return Array(nMels) { m ->
            // norm="slaney": cada filtro se escala para tener área constante.
            val scale = when (melNorm) {
                AudioConfig.MelNorm.SLANEY -> 2.0 / (hzPoints[m + 2] - hzPoints[m])
                AudioConfig.MelNorm.NONE -> 1.0
            }
            DoubleArray(nFreqs) { f ->
                val down = (allFreqs[f] - hzPoints[m]) / (hzPoints[m + 1] - hzPoints[m])
                val up = (hzPoints[m + 2] - allFreqs[f]) / (hzPoints[m + 2] - hzPoints[m + 1])
                max(0.0, min(down, up)) * scale
            }
        }
    }

    private fun hzToMel(hz: Double): Double = when (melScale) {
        AudioConfig.MelScale.HTK -> 2595.0 * log10(1.0 + hz / 700.0)
        AudioConfig.MelScale.SLANEY ->
            if (hz < SLANEY_MIN_LOG_HZ) hz / SLANEY_F_SP
            else SLANEY_MIN_LOG_MEL + ln(hz / SLANEY_MIN_LOG_HZ) / SLANEY_LOG_STEP
    }

    private fun melToHz(mel: Double): Double = when (melScale) {
        AudioConfig.MelScale.HTK -> 700.0 * (10.0.pow(mel / 2595.0) - 1.0)
        AudioConfig.MelScale.SLANEY ->
            if (mel < SLANEY_MIN_LOG_MEL) mel * SLANEY_F_SP
            else SLANEY_MIN_LOG_HZ * exp(SLANEY_LOG_STEP * (mel - SLANEY_MIN_LOG_MEL))
    }

    // 10*log10(max(x, 1e-10)) - 10*log10(ref), recortado a (máximo - top_db).
    private fun toDecibels(values: FloatArray) {
        val reference = when (dbReference) {
            AudioConfig.DbReference.MAX -> max(values.max().toDouble(), AMIN)
            AudioConfig.DbReference.ONE -> 1.0
        }
        val referenceDb = 10.0 * log10(reference)

        var maxDb = Float.NEGATIVE_INFINITY
        for (i in values.indices) {
            val db = (10.0 * log10(max(values[i].toDouble(), AMIN)) - referenceDb).toFloat()
            values[i] = db
            maxDb = max(maxDb, db)
        }

        val floor = (maxDb - topDb).toFloat()
        for (i in values.indices) {
            values[i] = max(values[i], floor)
        }
    }

    private fun normalize(values: FloatArray) {
        if (normalization != AudioConfig.Normalization.PER_SAMPLE_STANDARDIZE) return

        val mean = values.average()
        val std = sqrt(values.sumOf { (it - mean) * (it - mean) } / values.size)
        val safeStd = if (std < 1e-6) 1.0 else std
        for (i in values.indices) {
            values[i] = ((values[i] - mean) / safeStd).toFloat()
        }
    }

    private companion object {
        const val AMIN = 1e-10

        // Constantes de la escala mel de Slaney (librosa.hz_to_mel con htk=False).
        const val SLANEY_F_SP = 200.0 / 3.0
        const val SLANEY_MIN_LOG_HZ = 1000.0
        const val SLANEY_MIN_LOG_MEL = SLANEY_MIN_LOG_HZ / SLANEY_F_SP
        val SLANEY_LOG_STEP = ln(6.4) / 27.0
    }

    // FFT radix-2 iterativa, in-place.
    private fun fft(real: DoubleArray, imag: DoubleArray) {
        val n = real.size

        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                real[i] = real[j].also { real[j] = real[i] }
                imag[i] = imag[j].also { imag[j] = imag[i] }
            }
        }

        var length = 2
        while (length <= n) {
            val angle = -2.0 * PI / length
            val wReal = cos(angle)
            val wImag = sin(angle)
            var start = 0
            while (start < n) {
                var curReal = 1.0
                var curImag = 0.0
                for (k in 0 until length / 2) {
                    val a = start + k
                    val b = a + length / 2
                    val tReal = real[b] * curReal - imag[b] * curImag
                    val tImag = real[b] * curImag + imag[b] * curReal
                    real[b] = real[a] - tReal
                    imag[b] = imag[a] - tImag
                    real[a] += tReal
                    imag[a] += tImag
                    val nextReal = curReal * wReal - curImag * wImag
                    curImag = curReal * wImag + curImag * wReal
                    curReal = nextReal
                }
                start += length
            }
            length = length shl 1
        }
    }
}
