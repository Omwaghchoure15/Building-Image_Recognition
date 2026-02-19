package com.example.buildingimagerecognition.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import java.io.FileOutputStream
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

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        capture.takePicture(outputOptions, executor,
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    val bitmap = rotateImageIfRequired(photoFile)

                    if (bitmap != null) {
                        // Save the rotated bitmap back to the file
                        try {
                            FileOutputStream(photoFile).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            }
                        } catch (e: Exception) {
                            Log.e("CameraModel", "Failed to save rotated image", e)
                        }
                        onImageCaptured(bitmap, photoFile.absolutePath)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraModel", "Capture failed", exception)
                }
            }
        )
    }

    private fun rotateImageIfRequired(photoFile: File): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath) ?: return null
        
        val ei = ExifInterface(photoFile.absolutePath)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}