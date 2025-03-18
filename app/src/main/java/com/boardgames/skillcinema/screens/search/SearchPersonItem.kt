package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.boardgames.skillcinema.data.remote.PersonDetailResponse

@Composable
fun SearchPersonItem(
    person: PersonDetailResponse,
    onPersonClick: (PersonDetailResponse) -> Unit
) {
    // Имя персоны – берем русское, если есть, иначе английское
    val name = person.nameRu ?: person.nameEn ?: "Имя отсутствует"
    val imageUrl = person.posterUrl
    val profession = person.profession ?: "Профессия не указана"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onPersonClick(person) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Постер персоны
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Нет изображения",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Информация о персоне: имя и профессия
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = profession,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
