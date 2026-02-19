package com.example.buildingimagerecognition.ui.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.R
import com.example.buildingimagerecognition.data.BuildingEntity
import com.example.buildingimagerecognition.model.BuildingViewModel
import com.example.buildingimagerecognition.ui.theme.BuildingImageRecognitionTheme
import com.example.buildingimagerecognition.util.ImageUtils

@Preview(showBackground = true)
@Composable
fun BuildingListPreview(){
    BuildingImageRecognitionTheme {
        BuildingListContent(
            buildings = listOf(
                BuildingEntity(1, "Sample Building", "123 Main St", "Office,Modern", ""),
                BuildingEntity(2, "Another One", "456 Side St", "Residential,Old", "")
            ),
            onBack = {},
            onDeleteBuilding = {}
        )
    }
}

@Composable
fun BuildingListScreen(
    viewModel: BuildingViewModel,
    onBack: () -> Unit
) {
    val buildings by viewModel.allBuildings.collectAsState()

    // Delegate to a stateless content composable
    BuildingListContent(
        buildings = buildings,
        onBack = onBack,
        onDeleteBuilding = { viewModel.deleteBuilding(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingListContent(
    buildings: List<BuildingEntity>,
    onBack: () -> Unit,
    onDeleteBuilding: (BuildingEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Stored Buildings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${buildings.size} buildings saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (buildings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No buildings stored yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(buildings) { building ->
                    BuildingItem(
                        building = building,
                        onDelete = { onDeleteBuilding(building) }
                    )
                }
            }
        }
    }
}

@Composable
fun BuildingItem(
    building: BuildingEntity,
    onDelete: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstImagePath = remember(building.imagePaths) {
                building.imagePaths.split(",").firstOrNull { it.isNotBlank() }
            }
            
            val bitmap = remember(firstImagePath) {
                runCatching {
                    if (isPreview) {
                        BitmapFactory.decodeResource(context.resources, R.drawable.icons)
                    } else {
                        firstImagePath?.let { ImageUtils.decodeAndRotateImage(it) }
                    }
                }.getOrNull()
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = building.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = building.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Show labels as a joined string
                val labelText = building.labels.split(",").joinToString(", ")
                Text(
                    text = "Labels: $labelText",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}