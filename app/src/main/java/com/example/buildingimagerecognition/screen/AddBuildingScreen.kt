package com.example.buildingimagerecognition.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.model.BuildingViewModel
import com.example.buildingimagerecognition.ui.theme.BuildingImageRecognitionTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun AddBuildingScreenPreview() {
    BuildingImageRecognitionTheme {
        AddBuildingContent(
            capturedImagePath = "sample/path/image.jpg",
            onOpenCamera = {},
            onSaveBuildings = { _, _, _ -> },
            onSaved = {}
        )
    }
}


@Composable
fun AddBuildingScreen(
    viewModel: BuildingViewModel,
    onOpenCamera: () -> Unit,
    onSaved: () -> Unit
) {
    AddBuildingContent(
        capturedImagePath = viewModel.capturedImagePath,
        onOpenCamera = onOpenCamera,
        onSaveBuildings = { name, location, imagePaths ->

            val finalLabels =
                if (viewModel.detectedLabels.isNotEmpty()) {
                    viewModel.detectedLabels
                } else {
                    listOf("building", "architecture", "structure")
                }

            viewModel.saveBuildings(
                name = name,
                location = location,
                labels = finalLabels,
                imagePaths = imagePaths
            )
        },
        onSaved = onSaved
    )
}



@Composable
fun AddBuildingContent(
    capturedImagePath: String?,
    onOpenCamera: () -> Unit,
    onSaveBuildings: (String, String, List<String>) -> Unit,
    onSaved: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val imagePaths = remember { mutableStateListOf<String>() }
    var error by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Building Name") }
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") }
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (capturedImagePath != null && imagePaths.isEmpty()) {
                        imagePaths.add(capturedImagePath)
                    }
                }
            ) {
                Button(onClick = onOpenCamera) {
                    Text("Capture Image (${imagePaths.size}/3)")
                }
            }

            CapturedImagePreview(imagePaths)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onOpenCamera,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Camera")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    error = when {
                        name.isBlank() -> "Name required"
                        location.isBlank() -> "Location required"
                        imagePaths.isEmpty() -> "Capture at least 1 image"
                        else -> ""
                    }

                    if (error.isEmpty()) {
                        onSaveBuildings(
                            name,
                            location,
                            imagePaths
                        )
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "Building saved successfully ✅"
                            )
                            onSaved()
                        }
                    }
                }
            ) {
                Text("Save Building")
            }

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }
        }
    }
}

@Composable
fun CapturedImagePreview(imagePaths: List<String>) {
    if (imagePaths.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        items(imagePaths) { path ->
            val bitmap = remember(path) {
                try {
                    BitmapFactory.decodeFile(path)
                } catch (e: Exception) {
                    null
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Building image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}