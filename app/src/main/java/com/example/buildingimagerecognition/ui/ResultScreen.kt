package com.example.buildingimagerecognition.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.R
import com.example.buildingimagerecognition.data.BuildingEntity

@Composable
fun ResultScreen(
    building: BuildingEntity?,
    labels: List<String>,
    capturedImagePath: String?,
    onAddBuilding: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            capturedImagePath?.let {
                Text(
                    text = "Captured Image",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                ImageCarousel(imagePaths = listOf(it))

                Spacer(Modifier.height(16.dp))
            }

            if (building != null) {
                Text(
                    text = "Building Recognized",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(8.dp))

                Text("Name: ${building.name}")
                Text("Location: ${building.location}")

                if (building.imagePaths.isNotBlank()) {

                    Spacer(Modifier.height(16.dp))

                    Text("Reference Images:")

                    Spacer(Modifier.height(8.dp))

                    ImageCarousel(imagePaths = building.imagePaths.split(","))
                }

                Spacer(Modifier.height(16.dp))
                Text("Detected Labels:")
                Text(labels.joinToString(", "))

            } else {
                Text(
                    text = "No Building Match Found",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(8.dp))

                Text("Detected Labels:")
                Text(labels.joinToString(", "))

                Spacer(Modifier.height(16.dp))

                Button(onClick = onAddBuilding) {
                    Text("Add Building")
                }
            }
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun ImageCarousel(imagePaths: List<String>) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imagePaths) { path ->
            val bitmap = remember(path) {
                if (isPreview) {
                    BitmapFactory
                        .decodeResource(
                            context.resources,
                            R.drawable.img1
                        )
                } else {
                    try {
                        BitmapFactory.decodeFile(path)
                    } catch (_: Exception) {
                        null
                    }
                }
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Building image",
                    modifier = Modifier
                        .size(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    ResultScreen(
        building = null,
        labels = listOf("label1", "label2"),
        capturedImagePath = R.drawable.img1.toString(),
        onAddBuilding = {}
    )
}