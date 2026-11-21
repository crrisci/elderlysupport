package com.example.elderlysupport.medicine

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object MedicineOcr {
    fun recognize(context: Context, uri: Uri, callback: (Result<MedicineProposal>) -> Unit) {
        val image = try {
            InputImage.fromFilePath(context, uri)
        } catch (error: Exception) {
            callback(Result.failure(error))
            return
        }

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { result ->
                callback(Result.success(MedicineParser.parse(result.text)))
            }
            .addOnFailureListener { error -> callback(Result.failure(error)) }
            .addOnCompleteListener { recognizer.close() }
    }
}
