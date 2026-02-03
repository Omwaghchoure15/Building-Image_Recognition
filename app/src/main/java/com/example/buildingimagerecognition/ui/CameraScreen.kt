@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.buildingimagerecognition.ui

import android.graphics.Bitmap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.buildingimagerecognition.model.CameraModel

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.DarkGray, Color.Black)
                )
            )
    ) {
        CameraOverlay(onShutterClick = {})
    }
}

@Composable
fun CameraScreen(
    onImageCaptured: (Bitmap, String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({

                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                }, executor)

                previewView
            }
        )

        CameraOverlay {
            val capture = imageCapture ?: return@CameraOverlay
            CameraModel.captureImage(
                context = context,
                imageCapture = capture,
                executor = executor
            ) { bitmap, path ->
                onImageCaptured(bitmap, path)
            }
        }
    }
}

@Composable
fun ShutterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .border(
                width = 4.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
    }
}
@Composable
fun CameraOverlay(
    onShutterClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )

        Text(
            text = "Align the building within the frame",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 44.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            ShutterButton(onClick = onShutterClick)
        }
    }
}