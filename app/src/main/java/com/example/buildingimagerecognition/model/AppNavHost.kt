package com.example.buildingimagerecognition.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buildingimagerecognition.screen.AddBuildingScreen
import com.example.buildingimagerecognition.screen.CameraScreen
import com.example.buildingimagerecognition.screen.HomeScreen
import com.example.buildingimagerecognition.screen.ResultScreen

@Composable
fun AppNavHost(
    viewModel: BuildingViewModel
) {
    val navController = rememberNavController()
    var cameraSource by remember { mutableStateOf(CameraSource.SCAN) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen {
                cameraSource = CameraSource.SCAN
                navController.navigate(Screen.Camera.route)
            }
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                cameraSource = cameraSource
            ) { bitmap, imagePath ->

                if (cameraSource == CameraSource.SCAN) {
                    navController.navigate(Screen.Result.route)
                } else {
                    navController.popBackStack()
                }
            }
        }


        composable(Screen.Result.route) {
            ResultScreen(
                building = viewModel.matchedBuilding,
                labels = viewModel.detectedLabels,
                capturedImagePath = viewModel.capturedImagePath,
                onAddBuilding = {
                    cameraSource = CameraSource.ADD_BUILDING
                    navController.navigate(Screen.AddBuilding.route)
                }
            )
        }

        composable(Screen.AddBuilding.route) {
            AddBuildingScreen(
                viewModel = viewModel,
                onOpenCamera = {
                    cameraSource = CameraSource.ADD_BUILDING
                    navController.navigate(Screen.Camera.route)
                },
                onSaved = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}