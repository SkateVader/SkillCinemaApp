package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.UiState

@Composable
fun MovieCarousel(
    title: String,
    uiState: UiState<List<Movie>>,
    onMovieClick: (Movie) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        CollectionHeader(title = title, onViewAll = onViewAll)
        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            is UiState.Error -> Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
            is UiState.Success -> {
                val movies = uiState.data
                if (movies.isEmpty()) {
                    Text(
                        text = "Нет данных",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyRow(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movies) { movie ->
                            MovieItem(movie = movie, onMovieClick = onMovieClick)
                        }
                        if (uiState.hasMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .height(180.dp) // высота постера
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ViewAllButton(onViewAll = onViewAll)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViewAllButton(onViewAll: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clickable { onViewAll() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Круглая кнопка с иконкой стрелки
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp) // Диаметр круга
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Показать все",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Текст под кнопкой, помещается в одну строку
        Text(
            text = "Показать все",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

