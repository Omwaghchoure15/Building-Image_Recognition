package com.example.buildingimagerecognition.screen

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import com.example.buildingimagerecognition.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
        onAddBuilding = {},
    )
}

@Composable
fun ResultScreen(
    building: BuildingEntity?,
    labels: List<String>,
    onAddBuilding: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (building != null) {

                val images = getImageItems(building)
                ImageCarousel(images)

                Spacer(Modifier.height(16.dp))

                Text(
                    "Building Recognized",
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
                    "No Building Match Found",
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
fun ImageCarousel(images: List<Any>) {
    val context = LocalContext.current

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(images) { item ->

            val bitmap = remember(item) {
                try {
                    when (item) {
                        is Int -> BitmapFactory.decodeResource(
                            context.resources,
                            item
                        )
                        is String -> BitmapFactory.decodeFile(item)
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .width(300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun getImageItems(building: BuildingEntity?): List<Any> {
    return if (building == null || building.imagePaths.isBlank()) {
        listOf(
            R.drawable.img1,
            R.drawable.img2,
        )
    } else {
        building.imagePaths.split(",")
    }
}



