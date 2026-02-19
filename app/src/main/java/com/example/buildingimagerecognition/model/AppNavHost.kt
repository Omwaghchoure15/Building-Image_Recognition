package com.example.buildingimagerecognition.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buildingimagerecognition.ui.screen.AddBuildingScreen
import com.example.buildingimagerecognition.ui.screen.BuildingListScreen
import com.example.buildingimagerecognition.ui.screen.CameraScreen
import com.example.buildingimagerecognition.ui.screen.HomeScreen
import com.example.buildingimagerecognition.ui.screen.ResultScreen

@Composable
fun AppNavHost(
    viewModel: BuildingViewModel
) {
    val navController = rememberNavController()
    var cameraSource by remember { mutableStateOf(CameraSource.SCAN) }

    NavHost(
        navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {
                    cameraSource = CameraSource.SCAN
                    navController.navigate(Screen.Camera.create("home"))
                },
                onViewListClick = {
                    navController.navigate("building_list")
                }
            )
        }

        composable("building_list") {
            BuildingListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen { bitmap, imagePath ->

                when (cameraSource) {
                    CameraSource.SCAN -> {
                        viewModel.setCapturedImage(imagePath)
                        viewModel.processCapturedImage(bitmap)
                        navController.navigate(Screen.Result.route) {
                            popUpTo(Screen.Camera.route) { inclusive = true }
                        }
                    }

                    CameraSource.ADD_BUILDING -> {
                        viewModel.addCapturedImage(imagePath)
                        viewModel.processCapturedImage(bitmap)
                        navController.popBackStack()
                    }
                }
            }
        }

        composable(Screen.Result.route) {
            ResultScreen(
                building = viewModel.matchedBuilding,
                labels = viewModel.detectedLabels,
                capturedImagePath = viewModel.capturedImagePath,
                onAddBuilding = {
                    // Pre-populate with the image that was just scanned
                    viewModel.capturedImagePath?.let { viewModel.addCapturedImage(it) }
                    navController.navigate(Screen.AddBuilding.route)
                }
            )
        }

        composable(Screen.AddBuilding.route) {
            AddBuildingScreen(
                viewModel = viewModel,
                onOpenCamera = {
                    cameraSource = CameraSource.ADD_BUILDING
                    navController.navigate(Screen.Camera.create("add"))
                },
                onSaved = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }
    }
}