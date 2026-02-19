package com.example.buildingimagerecognition.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File

object ImageUtils {
    private const val TAG = "ImageUtils"

    fun decodeAndRotateImage(path: String): Bitmap? {
        val file = File(path)
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: $path")
            return null
        }

        val bitmap = BitmapFactory.decodeFile(path)
        if (bitmap == null) {
            Log.e(TAG, "Failed to decode file: $path")
            return null
        }

        val ei = try {
            ExifInterface(path)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read EXIF data for: $path", e)
            return bitmap // Return bitmap without rotation
        }

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