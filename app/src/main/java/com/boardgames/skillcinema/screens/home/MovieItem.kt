package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import com.boardgames.skillcinema.screens.details.MovieItemDetailsViewModel

@Composable
fun MovieItem(
    movie: Movie,
    onMovieClick: (Movie) -> Unit,
) {
    val title = movie.title ?: "Название отсутствует"
    val imageUrl = movie.imageUrl

    // Получаем CollectionsViewModel для проверки просмотренных фильмов
    val collectionsViewModel: CollectionsViewModel = hiltViewModel()
    val watchedList by collectionsViewModel.watched.collectAsState()
    val isWatched = watchedList.any { it.id == movie.id }

    // Получаем общий экземпляр MovieItemDetailsViewModel
    val itemDetailsViewModel: MovieItemDetailsViewModel = hiltViewModel()
    // Загружаем рейтинг для данного фильма (если ещё не загружен)
    LaunchedEffect(movie.id) {
        itemDetailsViewModel.loadDetails(movie.id)
    }
    val ratingsMap by itemDetailsViewModel.ratings.collectAsState()
    // Если в кэше есть рейтинг для movie.id, берем его, иначе используем значение из movie
    val rating = ratingsMap[movie.id] ?: movie.ratingKinopoisk

    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onMovieClick(movie) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
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
            // Отображаем рейтинг, если он есть
            rating?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
                        .zIndex(2f)
                ) {
                    Text(
                        text = it.toString(),
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            // Отображаем иконку "просмотрено", если фильм отмечен
            if (isWatched) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye),
                    contentDescription = "Просмотрено",
                    tint = Color.Yellow,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .zIndex(2f)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }
}
