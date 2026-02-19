package com.example.buildingimagerecognition.ui.screen

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.R
import com.example.buildingimagerecognition.model.BuildingViewModel
import com.example.buildingimagerecognition.util.ImageUtils
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun AddBuildingScreenPreview() {
    AddBuildingContent(
        capturedImages = listOf(),
        onOpenCamera = {},
        onRemoveImage = {},
        onSaveBuildings = { _, _, _ -> },
        onSaved = {}
    )
}
@Composable
fun AddBuildingScreen(
    viewModel: BuildingViewModel,
    onOpenCamera: () -> Unit,
    onSaved: () -> Unit
) {
    AddBuildingContent(
        capturedImages = viewModel.capturedImages,
        onOpenCamera = onOpenCamera,
        onRemoveImage = { viewModel.removeImage(it) },
        onSaveBuildings = { name, location, imagePaths ->
            val finalLabels = viewModel.detectedLabels.ifEmpty {
                listOf("building", "architecture", "structure")
            }
            viewModel.saveBuildings(
                name = name,
                location = location,
                labels = finalLabels,
                imagePaths = imagePaths
            )
            viewModel.clearImages()
        },
        onSaved = onSaved
    )
}

@Composable
fun AddBuildingContent(
    capturedImages: List<String>,
    onOpenCamera: () -> Unit,
    onRemoveImage: (String) -> Unit,
    onSaveBuildings: (String, String, List<String>) -> Unit,
    onSaved: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            if (capturedImages.isNotEmpty()) {
                Text(
                    text = "Captured Images (${capturedImages.size}/3)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                CapturedImagePreview(capturedImages, onRemoveImage)
                Spacer(Modifier.height(16.dp))
            } else {
                Text(
                    text = "No images captured yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Building Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onOpenCamera,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = capturedImages.size < 3,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (capturedImages.isEmpty())
                        "Capture Image"
                    else if (capturedImages.size < 3)
                        "Add Another Image"
                    else
                        "Maximum 3 Images Captured",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    error = when {
                        name.isBlank() -> "Please enter building name"
                        location.isBlank() -> "Please enter location"
                        capturedImages.isEmpty() -> "Please capture at least one image"
                        else -> ""
                    }

                    if (error.isEmpty()) {
                        // Create a copy of the list before passing it to avoid 
                        // issues with mutable lists being cleared in the ViewModel
                        onSaveBuildings(
                            name,
                            location,
                            capturedImages.toList()
                        )

                        scope.launch {
                            snackBarHostState.showSnackbar(
                                "Building saved successfully ✅"
                            )
                            onSaved()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Building", style = MaterialTheme.typography.titleMedium)
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            // Extra spacer to ensure content isn't cut off when keyboard is up
            Spacer(Modifier.height(32.dp))
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun CapturedImagePreview(
    imagePaths: List<String>,
    onRemoveImage: (String) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imagePaths) { path ->
            val bitmap = remember(path) {
                runCatching {
                    if (isPreview) {
                        BitmapFactory.decodeResource(context.resources, R.drawable.icons)
                    } else {
                        ImageUtils.decodeAndRotateImage(path)
                    }
                }.getOrNull()
            }
            if (bitmap != null) {
                Box {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Building image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(12.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveImage(path) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .padding(4.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.5f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}