package com.example.buildingimagerecognition.screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.buildingimagerecognition.ui.theme.BuildingImageRecognitionTheme

@Composable
fun GalleryScreen(
    onImageSelected: (Bitmap, String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            onImageSelected(bitmap, uri.toString())
        } else {
            onCancel()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }

    Box(modifier = Modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun GalleryScreenPreview() {
    BuildingImageRecognitionTheme {
        GalleryScreen(
            onImageSelected = { _, _ -> },
            onCancel = {}
        )
    }
}
