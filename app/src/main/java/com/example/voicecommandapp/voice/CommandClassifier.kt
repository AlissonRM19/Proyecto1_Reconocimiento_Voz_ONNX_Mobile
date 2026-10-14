package com.example.voicecommandapp.voice

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.nio.FloatBuffer
import kotlin.math.abs
import kotlin.math.exp

/** Ejecuta el modelo exportado a ONNX localmente con ONNX Runtime Mobile. */
class CommandClassifier private constructor(
    private val environment: OrtEnvironment,
    private val session: OrtSession,
    val labels: List<String>
) : AutoCloseable {

    private val inputName = session.inputNames.first()
    private val inputShape: LongArray =
        (session.inputInfo.getValue(inputName).info as TensorInfo).shape

    /**
     * Recibe el espectrograma aplanado [mel][frame] y devuelve una
     * probabilidad por cada etiqueta de [labels].
     */
    fun classify(features: FloatArray, nMels: Int, frames: Int): FloatArray {
        // Los ejes dinámicos (batch) vienen como -1; se usa 1.
        val shape = LongArray(inputShape.size) { i -> if (inputShape[i] > 0) inputShape[i] else 1L }
        val expected = shape.fold(1L) { acc, d -> acc * d }
        require(expected == features.size.toLong()) {
            "Model expects input ${inputShape.contentToString()}, " +
                "but preprocessing produces [1, 1, $nMels, $frames]. Check AudioConfig."
        }

        OnnxTensor.createTensor(environment, FloatBuffer.wrap(features), shape).use { tensor ->
            session.run(mapOf(inputName to tensor)).use { result ->
                @Suppress("UNCHECKED_CAST")
                val scores = (result[0].value as Array<FloatArray>)[0]
                require(scores.size == labels.size) {
                    "Model has ${scores.size} outputs but labels.txt has ${labels.size} labels"
                }
                return if (looksLikeProbabilities(scores)) scores else softmax(scores)
            }
        }
    }

    override fun close() {
        session.close()
    }

    private fun looksLikeProbabilities(scores: FloatArray): Boolean =
        scores.all { it in 0f..1f } && abs(scores.sum() - 1f) < 1e-3f

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.max()
        val exps = FloatArray(logits.size) { exp(logits[it] - maxLogit) }
        val total = exps.sum()
        return FloatArray(exps.size) { exps[it] / total }
    }

    companion object {

        fun load(context: Context): CommandClassifier {
            val modelFile = try {
                copyAsset(context, AudioConfig.MODEL_ASSET)
            } catch (e: FileNotFoundException) {
                throw IllegalStateException(
                    "Model not found. Add ${AudioConfig.MODEL_ASSET} to app/src/main/assets", e
                )
            }

            // torch.onnx.export puede guardar los pesos aparte en "<modelo>.data";
            // ONNX Runtime lo busca junto al modelo, por eso se carga desde archivo.
            try {
                copyAsset(context, AudioConfig.MODEL_ASSET + ".data")
            } catch (_: FileNotFoundException) {
                // El modelo tiene los pesos embebidos.
            }

            // Una etiqueta por línea, en el mismo orden que las salidas del modelo.
            val labels = context.assets.open(AudioConfig.LABELS_ASSET).bufferedReader().useLines { lines ->
                lines.map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .toList()
            }

            val environment = OrtEnvironment.getEnvironment()
            val session = environment.createSession(modelFile.absolutePath, OrtSession.SessionOptions())
            return CommandClassifier(environment, session, labels)
        }

        private fun copyAsset(context: Context, name: String): File {
            val target = File(context.noBackupFilesDir, name)
            context.assets.open(name).use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            return target
        }
    }
}
