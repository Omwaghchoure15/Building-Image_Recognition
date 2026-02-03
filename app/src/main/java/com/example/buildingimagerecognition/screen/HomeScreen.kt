package com.example.buildingimagerecognition.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Building Image Recognition",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Scan buildings using AI and recognize them instantly",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .height(52.dp)
            ) {
                Text(
                    text = "Scan Building",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGalleryClick,
                modifier = Modifier
                    .height(52.dp)
            ) {
                Text(
                    text = "Select from Gallery",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
