package com.example.buildingimagerecognition

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildingimagerecognition.model.AppNavHost
import com.example.buildingimagerecognition.model.BuildingViewModel

class MainActivity : ComponentActivity() {
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted ->
        if (granted) {
            startApp()

        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasCameraPermission()) {
            if (hasGalleryPermission()) {
                startApp()
            } else {
                galleryPermissionLauncher.launch(getGalleryPermission())
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startApp() {
        setContent {
            val buildingViewModel: BuildingViewModel = viewModel()
            AppNavHost(viewModel = buildingViewModel)
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            getGalleryPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getGalleryPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}
