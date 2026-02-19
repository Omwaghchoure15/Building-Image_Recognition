package com.example.buildingimagerecognition.ui.screen

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildingimagerecognition.R
import com.example.buildingimagerecognition.data.BuildingEntity
import com.example.buildingimagerecognition.ui.theme.BuildingImageRecognitionTheme
import com.example.buildingimagerecognition.util.ImageUtils

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    BuildingImageRecognitionTheme {
        ResultScreen(
            building = BuildingEntity(
                name = "Nutan 32 Number",
                location = "Abhyuday nager Kalachowki",
                labels = "building",
                imagePaths = ""
            ),
            labels = listOf("building"),
            capturedImagePath = R.drawable.img1.toString(),
            onAddBuilding = {}
        )
    }
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
                .padding(20.dp)
        ) {
            capturedImagePath?.let {
                Text(
                    text = "Captured Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(12.dp))

                ImageCarousel(imagePaths = listOf(it))

                Spacer(Modifier.height(24.dp))
            }

            if (building != null) {
                Text(
                    text = "Building Recognized",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Name: ${building.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Location: ${building.location}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (building.imagePaths.isNotBlank()) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Reference Images",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(12.dp))
                    ImageCarousel(imagePaths = building.imagePaths.split(","))
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Detected Labels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = labels.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {
                Text(
                    text = "No Building Match Found",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Detected Labels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = labels.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onAddBuilding,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Add This Building", style = MaterialTheme.typography.titleMedium)
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
                    BitmapFactory.decodeResource(context.resources, R.drawable.img1)
                } else {
                    ImageUtils.decodeAndRotateImage(path)
                }
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Building image",
                    modifier = Modifier
                        .size(width = 250.dp, height = 200.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
