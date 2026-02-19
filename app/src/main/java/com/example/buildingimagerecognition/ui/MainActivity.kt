package com.example.buildingimagerecognition.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildingimagerecognition.model.AppNavHost
import com.example.buildingimagerecognition.model.BuildingViewModel
import com.example.buildingimagerecognition.ui.theme.BuildingImageRecognitionTheme

class MainActivity : ComponentActivity() {
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startApp()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasCameraPermission()) {
            startApp()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }

    private fun startApp() {
        setContent {
            BuildingImageRecognitionTheme {
                val buildingViewModel: BuildingViewModel = viewModel()
                AppNavHost(viewModel = buildingViewModel)
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
