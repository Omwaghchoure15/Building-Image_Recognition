package com.example.buildingimagerecognition.ui

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

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
            val finalLabels = viewModel.detectedLabels.ifEmpty {
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
    var error by remember { mutableStateOf("") }

    val imagePaths = remember(capturedImagePath) {
        if (capturedImagePath != null) listOf(capturedImagePath)
        else emptyList()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (imagePaths.isNotEmpty()) {
                Text(
                    text = "Captured Image",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                CapturedImagePreview(imagePaths)
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Building Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onOpenCamera,
                modifier = Modifier.height(52.dp)
            ) {
                Text(
                    if (capturedImagePath == null)
                        "Capture Image"
                    else
                        "Retake Image ✔"
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    error = when {
                        name.isBlank() -> "Name required"
                        location.isBlank() -> "Location required"
                        capturedImagePath == null -> "Capture image first"
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
                                "Building saved successfully ✅"
                            )
                            onSaved()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Save Building")
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = Color.Red)
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun CapturedImagePreview(imagePaths: List<String>) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imagePaths) { path ->
            val bitmap = remember(path) {
                runCatching {
                    if (isPreview) {
                        BitmapFactory.decodeResource(context.resources, R.drawable.img1)
                    } else {
                        BitmapFactory.decodeFile(path)
                    }
                }.getOrNull()
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Building image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBuildingScreenPreview() {
    AddBuildingContent(
        capturedImagePath = R.drawable.img1.toString(),
        onOpenCamera = {},
        onSaveBuildings = { _, _, _ -> },
        onSaved = {}
    )
}
