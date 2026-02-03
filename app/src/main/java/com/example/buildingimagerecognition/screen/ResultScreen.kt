package com.example.buildingimagerecognition.screen

import android.graphics.BitmapFactory
import com.example.buildingimagerecognition.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.data.BuildingEntity


@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    ResultScreen(
        building = null,
        labels = listOf("label1", "label2"),
        capturedImagePath = null,
        onAddBuilding = {},
    )
}

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

            // ✅ IMAGE SECTION (always visible)
            val images = when {
                building != null && building.imagePaths.isNotBlank() ->
                    building.imagePaths.split(",")

                capturedImagePath != null ->
                    listOf(capturedImagePath)

                else -> emptyList()
            }

            if (images.isNotEmpty()) {
                ImageCarousel(imagePaths = images)
                Spacer(Modifier.height(16.dp))
            }

            // ✅ CONTENT SECTION
            if (building != null) {

                Text(
                    text = "Building Recognized",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(8.dp))

                Text("Name: ${building.name}")
                Text("Location: ${building.location}")

                Spacer(Modifier.height(8.dp))

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

@Composable
fun ImageCarousel(imagePaths: List<String>) {
    val context = LocalContext.current

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imagePaths) { path ->

            val bitmap = remember(path) {
                try {
                    if (path.startsWith("drawable:")) {
                        val resName = path.removePrefix("drawable:")
                        val resId = context.resources.getIdentifier(
                            resName,
                            "drawable",
                            context.packageName
                        )
                        BitmapFactory.decodeResource(context.resources, resId)
                    } else {
                        BitmapFactory.decodeFile(path)
                    }
                } catch (e: Exception) {
                    null
                }
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Building image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun getImageItems(building: BuildingEntity?): List<String> {
    return if (building == null || building.imagePaths.isBlank()) {
        listOf(
            "drawable:img1",
            "drawable:img2"
        )
    } else {
        building.imagePaths.split(",")
    }
}