package com.example.buildingimagerecognition.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import java.util.concurrent.Executor

object CameraModel {

    fun captureImage(
        context: Context,
        imageCapture: ImageCapture?,
        executor: Executor,
        onImageCaptured: (Bitmap, String) -> Unit
    ) {
        val capture = imageCapture ?: return

        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "IMG_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    val bitmap =
                        BitmapFactory.decodeFile(photoFile.absolutePath)

                    if (bitmap != null) {
                        onImageCaptured(bitmap, photoFile.absolutePath)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraModel", "Capture failed", exception)
                }
            }
        )
    }
}

