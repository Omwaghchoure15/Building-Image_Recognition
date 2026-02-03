package com.example.buildingimagerecognition.model

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera/{from}") {
        fun create(from: String) = "camera/$from"
    }
    object Result : Screen("result")
    object AddBuilding : Screen("addBuilding")
}


