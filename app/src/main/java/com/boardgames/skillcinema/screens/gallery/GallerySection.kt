package com.boardgames.skillcinema.screens.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.boardgames.skillcinema.data.remote.GalleryItem

@Composable
fun GallerySection(
    total: Int,
    images: List<GalleryItem>,
    movieId: Int,
    navController: NavController
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Галерея", style = MaterialTheme.typography.titleMedium)
            if (total > 20) {
                Text(
                    text = "$total >",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        navController.navigate("movieImagesScreen/${movieId}")
                    }
                )
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(images.take(20)) { image ->
                AsyncImage(
                    model = image.url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            navController.navigate(
                                "fullScreenImage?movieId=" +
                                        "${movieId}&type=STILL&initialImageUrl=${image.url}"
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
