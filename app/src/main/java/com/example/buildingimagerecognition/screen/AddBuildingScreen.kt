package com.example.buildingimagerecognition.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.model.BuildingViewModel
import kotlinx.coroutines.launch


@Composable
fun AddBuildingScreen(
    viewModel: BuildingViewModel,
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
    ) {
        padding ->

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
                    val path = viewModel.capturedImagePath
                    if (path != null && imagePaths.size < 3) {
                        imagePaths.add(path)
                    }
                }
            ) {
                Text("Capture Image (${imagePaths.size}/3)")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    error = when {
                        name.isBlank() -> "Name required"
                        location.isBlank() -> "Location required"
                        imagePaths.size < 2 -> "Capture at least 2 images"
                        else -> ""
                    }

                    if (error.isEmpty()) {
                        viewModel.saveBuildings(
                            name = name,
                            location = location,
                            labels = listOf("building"),
                            imagePaths = imagePaths
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



