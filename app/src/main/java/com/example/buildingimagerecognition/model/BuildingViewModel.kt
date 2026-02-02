package com.example.buildingimagerecognition.model

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildingimagerecognition.data.AppDatabase
import com.example.buildingimagerecognition.data.BuildingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BuildingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao: BuildingDao =
        AppDatabase.getDatabase(application).buildingDao()

    var capturedImagePath by mutableStateOf<String?>(null)
        private set

    var matchedBuilding by mutableStateOf<BuildingEntity?>(null)
        private set

    var detectedLabels by mutableStateOf<List<String>>(emptyList())
        private set

    fun setCapturedImage(path: String) {
        capturedImagePath = path
    }

    fun processCapturedImage(bitmap: Bitmap) {
        MLKitHelper.labelImage(bitmap) { labels ->

            detectedLabels = labels.map { it.trim().lowercase() }

            viewModelScope.launch {
                val buildings = dao.getAllBuildings()

                matchedBuilding = buildings.firstOrNull { building ->

                    val storedLabels = building.labels
                        .split(",")
                        .map { it.trim().lowercase() }

                    storedLabels
                        .intersect(detectedLabels.toSet())
                        .size >= 2
                }
            }
        }
    }

    fun saveBuildings(
        name: String,
        location: String,
        labels: List<String>,
        imagePaths: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertBuilding(
                BuildingEntity(
                    name = name,
                    location = location,
                    labels = labels.joinToString(","),
                    imagePaths = imagePaths.joinToString(",")
                )
            )

            val allBuildings = dao.getAllBuildings()
            Log.d("ROOM", "All buildings: $allBuildings")
        }
    }


}
