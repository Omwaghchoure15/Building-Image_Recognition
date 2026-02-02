package com.example.buildingimagerecognition.model

import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            HomeScreen {
                navController.navigate(Screen.Camera.route)
            }
        }

        composable(Screen.Camera.route) {
            CameraScreen { bitmap, imagePath ->

                viewModel.setCapturedImage(imagePath)
                viewModel.processCapturedImage(bitmap)

                navController.navigate(Screen.Result.route)
            }
        }

        composable(Screen.Result.route) {
            ResultScreen(
                building = viewModel.matchedBuilding,
                labels = viewModel.detectedLabels,
                onAddBuilding = {
                    navController.navigate(Screen.AddBuilding.route)
                }
            )
        }

        composable(Screen.AddBuilding.route) {

            AddBuildingScreen(
                viewModel = viewModel,
                onSaved = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}