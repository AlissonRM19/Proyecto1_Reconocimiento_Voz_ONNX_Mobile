package com.example.voicecommandapp.voice

/**
 * Pipeline de entrada del modelo en producción. Debe ser idéntico al usado
 * en el notebook de entrenamiento; si cambia algo allá, cámbielo aquí.
 *
 * Equivalente en Python (librosa, valores por defecto con sr=16000):
 *
 * ```python
 * y, sr = librosa.load(path, sr=16000)              # float32 en [-1, 1]
 * y = librosa.util.fix_length(y, size=16000)        # clips de 1 s exactos
 * S = librosa.feature.melspectrogram(y=y, sr=16000, n_mels=128)
 *     # n_fft=2048, hop_length=512, center=True, pad_mode="constant", htk=False, norm="slaney"
 * x = librosa.power_to_db(S)                        # ref=1.0, top_db=80 → [128, 32]
 * x = x[np.newaxis, np.newaxis]                     # entrada del modelo: [1, 1, 128, 32]
 * ```
 */
object AudioConfig {

    const val SAMPLE_RATE = 16_000

    // Los clips del dataset duran 1 s. Se graba un poco más y se recorta el
    // segundo con más energía, así no importa si el usuario habla un poco tarde.
    const val CLIP_SAMPLES = 16_000
    const val RECORD_SAMPLES = 24_000

    const val N_FFT = 2048
    const val WIN_LENGTH = 2048
    const val HOP_LENGTH = 512
    const val N_MELS = 128
    const val F_MIN = 0.0
    const val F_MAX = 8_000.0
    const val TOP_DB = 80.0

    val PAD_MODE = PadMode.CONSTANT
    val MEL_SCALE = MelScale.SLANEY
    val MEL_NORM = MelNorm.SLANEY
    val DB_REFERENCE = DbReference.ONE
    val NORMALIZATION = Normalization.NONE

    // Por debajo de este RMS se considera que no se habló.
    const val SILENCE_RMS = 0.001f

    // Grabaciones bajas (emulador, micrófono lejano) se amplifican hasta este
    // pico, similar al volumen de los clips del dataset. Nunca se atenúa.
    const val TARGET_PEAK = 0.5f
    const val MAX_GAIN = 50f

    // Predicciones con menor probabilidad se descartan en lugar de ejecutarse.
    const val MIN_CONFIDENCE = 0.5f

    const val MODEL_ASSET = "modelo_comandos_voz.onnx"
    const val LABELS_ASSET = "labels.txt"

    // Relleno de center=True: librosa >= 0.10 usa ceros, torchaudio usa reflect.
    enum class PadMode { CONSTANT, REFLECT }

    // librosa usa Slaney por defecto (htk=False); torchaudio usa HTK.
    enum class MelScale { SLANEY, HTK }

    // librosa normaliza los filtros con norm="slaney"; torchaudio por defecto no.
    enum class MelNorm { SLANEY, NONE }

    // librosa.power_to_db(ref=np.max) vs ref=1.0 / torchaudio AmplitudeToDB.
    enum class DbReference { MAX, ONE }

    enum class Normalization {
        NONE,

        // (x - media) / desviación estándar, calculado por espectrograma.
        PER_SAMPLE_STANDARDIZE
    }
}
