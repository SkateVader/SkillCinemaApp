package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.boardgames.skillcinema.R
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel
import com.boardgames.skillcinema.screens.moviesDetails.MovieItemDetailsViewModel

@Composable
fun SearchMovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    val title = movie.title ?: "Название отсутствует"
    val imageUrl = movie.imageUrl
    val year = movie.year?.toString() ?: "Неизвестный год"
    val genre = movie.genre ?: "Неизвестный жанр"

    // Проверяем, добавлен ли фильм в "Просмотренные"
    val collectionsViewModel: CollectionsViewModel = hiltViewModel()
    val watchedList by collectionsViewModel.watched.collectAsState()
    val isWatched = watchedList.any { it.id == movie.id }

    // Загружаем рейтинг фильма (если ещё не загружен)
    val itemDetailsViewModel: MovieItemDetailsViewModel = hiltViewModel()
    LaunchedEffect(movie.id) {
        itemDetailsViewModel.loadDetails(movie.id)
    }
    val ratingsMap by itemDetailsViewModel.ratings.collectAsState()
    val rating = ratingsMap[movie.id]?.let { ratingValue ->
        "%.1f".format(ratingValue) // ratingValue уже Float
    } ?: movie.ratingKinopoisk?.let { kinopoiskRating ->
        "%.1f".format(kinopoiskRating) // kinopoiskRating уже Float
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onMovieClick(movie) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Постер фильма с рейтингом и значком "Просмотрено"
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет изображения",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ✅ Показываем рейтинг в `TopEnd`
            rating?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp))
                        .zIndex(2f)
                ) {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // ✅ Показываем иконку "Просмотрено" в `BottomEnd`
            if (isWatched) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = "Просмотрено",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(20.dp)
                        .zIndex(2f)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Информация о фильме (справа от постера)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$year, $genre",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
