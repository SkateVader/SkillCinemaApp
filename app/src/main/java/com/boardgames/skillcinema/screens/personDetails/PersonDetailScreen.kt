package com.boardgames.skillcinema.screens.personDetails

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.home.MovieItem

private const val TAG = "PersonDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    navController: NavController,
    staffId: Int?
) {
    if (staffId == null) {
        Log.e(TAG, "staffId is null. API call aborted.")
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Детали персоны") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("ID персоны не указан")
            }
        }
        return
    }

    val viewModel: PersonDetailViewModel = hiltViewModel()

    LaunchedEffect(staffId) {
        Log.d(TAG, "Запуск загрузки данных персоны для id: $staffId")
        viewModel.loadPersonDetail(staffId)
    }

    val personDetailState
            by viewModel.personDetailState.collectAsState()
    val bestMoviesState by viewModel.bestMoviesState.collectAsState()
    val totalFilmCount by viewModel.totalFilmCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали персоны") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (personDetailState) {
                is UiState.Loading -> {
                    Log.d(TAG, "Состояние загрузки для id: $staffId")
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val detail = (personDetailState as UiState.Success).data
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        // Верхняя часть: фото, имя и профессия
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = detail.posterUrl,
                                contentDescription = detail.posterUrl,
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(133.dp)
                                    .clickable {
                                        navController.navigate(
                                            "fullScreenImage?movieId=0&type=" +
                                                    "POSTER&initialImageUrl=${detail.posterUrl}"
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                (detail.nameRu ?: detail.nameEn)?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                detail.profession?.let { prof ->
                                    Text(
                                        text = prof,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Раздел "Лучшее"
                        when (bestMoviesState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator()
                            }

                            is UiState.Success -> {
                                val bestMovies =
                                    (bestMoviesState as UiState.Success).data
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Лучшее", style =
                                        MaterialTheme.typography.titleMedium
                                    )
                                    if (bestMovies.size >= 20 && totalFilmCount > 20) {
                                        TextButton(
                                            onClick = {
                                                navController.navigate(
                                                    "fullBestMovies/${detail.personId}"
                                                )
                                            }
                                        ) {
                                            Text(
                                                "Все", style =
                                                MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(bestMovies) { movie ->
                                        MovieItem(movie = movie) {
                                            navController.navigate("details/${movie.id}")
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Фильмография", style =
                                        MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "К списку >",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable {
                                            Log.d(
                                                TAG, "Переход к полному списку" +
                                                        " фильмографии для id: ${detail.personId}"
                                            )
                                            navController.navigate(
                                                "filmography/${
                                                    detail.personId
                                                }"
                                            )
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val filmCount = totalFilmCount
                                val filmDeclension = getFilmDeclension(filmCount)
                                Text(
                                    text = "$filmCount $filmDeclension",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            is UiState.Error -> {
                                val errorMessage = (bestMoviesState as UiState.Error).message
                                Text(errorMessage)
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    val errorMessage = (personDetailState as UiState.Error).message
                    Log.e(
                        TAG, "Ошибка при загрузке данных API для id: $staffId. Сообщение: " +
                                "$errorMessage"
                    )
                    Text(errorMessage)
                }
            }
        }
    }
}

fun getFilmDeclension(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> "фильм"
        mod10 in 2..4 && !(mod100 in 12..14) -> "фильма"
        else -> "фильмов"
    }
}