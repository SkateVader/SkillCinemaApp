package com.boardgames.skillcinema.screens.series

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Функция для форматирования даты (ожидается формат "yyyy-MM-dd")
fun formatDate(dateStr: String?): String {
    return if (dateStr != null) {
        try {
            val localDate = LocalDate.parse(dateStr)
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
            localDate.format(formatter)
        } catch (e: Exception) {
            dateStr // Возвращаем исходную строку, если парсинг не удался
        }
    } else {
        "Дата не указана" // Значение по умолчанию для null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesEpisodesScreen(
    navController: NavController,
    movieId: Int,
    seriesTitle: String,
    seasonsCount: Int
) {
    val viewModel: SeriesEpisodesViewModel = hiltViewModel()
    var selectedSeason by remember { mutableStateOf(1) }

    LaunchedEffect(movieId, selectedSeason) {
        viewModel.loadEpisodes(movieId, selectedSeason)
    }
    val episodesState by viewModel.episodes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = seriesTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Заголовок: выбор сезона и информация о количестве серий
            item {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Сезоны", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        for (season in 1..seasonsCount) {
                            com.boardgames.skillcinema.screens.gallery.FilterChipCell(
                                label = season.toString(),
                                isSelected = season == selectedSeason,
                                onClick = { selectedSeason = season },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "$selectedSeason сезон, ${viewModel.episodesCount} серий",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (episodesState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(
                            text = (episodesState as UiState.Error).message,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is UiState.Success -> {
                    val episodes = (episodesState as UiState.Success).data
                    items(episodes) { episode ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${episode.episodeNumber} серия. ${episode.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formatDate(episode.releaseDate),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

// Компонент для кнопок выбора сезона
@Composable
fun FilterChipCell(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.small
            )
            .then(
                if (!isSelected) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                ) else Modifier
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

