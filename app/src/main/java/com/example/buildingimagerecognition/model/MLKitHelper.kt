package com.example.buildingimagerecognition.model

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

object MLKitHelper {
    private val fallbackLabels = listOf(
        "building",
        "architecture",
        "structure"
    )
    fun labelImage(
        bitmap: Bitmap,
        onResult: (List<String>) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val labeler = ImageLabeling.getClient(
            ImageLabelerOptions.DEFAULT_OPTIONS
        )
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val result = labels
                    .filter { it.confidence > 0.6f }
                    .map { it.text.trim().lowercase() }

                if (result.isEmpty()) {
                    onResult(fallbackLabels)
                } else {
                    onResult(result)
                }
            }
            .addOnFailureListener {
                onResult(fallbackLabels)
            }
    }
}




