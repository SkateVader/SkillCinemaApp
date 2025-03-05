package com.boardgames.skillcinema.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.boardgames.skillcinema.data.remote.GalleryItem

@Composable
fun GallerySection(images: List<GalleryItem>) {
    Column {
        Text("Галерея", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            images.forEach { image ->
                Image(
                    painter = rememberImagePainter(image.url),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}
