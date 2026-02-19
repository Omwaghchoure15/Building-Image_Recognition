package com.example.buildingimagerecognition.model

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildingimagerecognition.data.AppDatabase
import com.example.buildingimagerecognition.data.BuildingDao
import com.example.buildingimagerecognition.data.BuildingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuildingViewModel(application: Application): AndroidViewModel(application) {
    private val dao: BuildingDao =
        AppDatabase.getDatabase(application).buildingDao()

    var capturedImagePath by mutableStateOf<String?>(null)
        private set

    var capturedImages = mutableStateListOf<String>()
        private set

    var matchedBuilding by mutableStateOf<BuildingEntity?>(null)
        private set

    var detectedLabels by mutableStateOf<List<String>>(emptyList())
        private set

    private val _allBuildings = MutableStateFlow<List<BuildingEntity>>(emptyList())
    val allBuildings: StateFlow<List<BuildingEntity>> = _allBuildings.asStateFlow()

    init {
        loadAllBuildings()
    }

    fun loadAllBuildings() {
        viewModelScope.launch(Dispatchers.IO) {
            _allBuildings.value = dao.getAllBuildings()
        }
    }

    fun setCapturedImage(path: String) {
        capturedImagePath = path
    }

    fun addCapturedImage(path: String) {
        if (capturedImages.size < 3) {
            capturedImages.add(path)
        }
    }

    fun removeImage(path: String) {
        capturedImages.remove(path)
    }

    fun clearImages() {
        capturedImages.clear()
        capturedImagePath = null
    }

    fun processCapturedImage(bitmap: Bitmap) {
        MLKitHelper.labelImage(bitmap) { labels ->
            detectedLabels = labels

            Log.d("ML_LABELS", "Detected labels: $labels")

            viewModelScope.launch(Dispatchers.IO) {
                val buildings = dao.getAllBuildings()
                val detectedSet = labels.map { it.trim().lowercase() }.toSet()

                // Find the building with the highest number of matching labels
                val bestMatch = buildings.map { building ->
                    val storedLabels = building.labels
                        .split(",")
                        .map { it.trim().lowercase() }
                        .toSet()
                    
                    val commonLabels = storedLabels.intersect(detectedSet)
                    building to commonLabels.size
                }
                .filter { it.second > 0 } // Must have at least one match
                .maxByOrNull { it.second } // Get the one with the most matches

                matchedBuilding = bestMatch?.first
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
            // Filter out any blank paths before joining
            val validPaths = imagePaths.filter { it.isNotBlank() }
            
            dao.insertBuilding(
                BuildingEntity(
                    name = name,
                    location = location,
                    labels = labels.joinToString(","),
                    imagePaths = validPaths.joinToString(",")
                )
            )
            loadAllBuildings()
            Log.d("ROOM", "Saved building with ${validPaths.size} images")
        }
    }

    fun deleteBuilding(building: BuildingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteBuilding(building)
            loadAllBuildings()
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAllBuildings()
            _allBuildings.value = emptyList()
            matchedBuilding = null
            detectedLabels = emptyList()
            clearImages()
        }
    }
}