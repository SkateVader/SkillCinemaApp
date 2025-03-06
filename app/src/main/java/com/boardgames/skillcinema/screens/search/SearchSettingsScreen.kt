package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsScreen(navController: NavController) {
    val viewModel: SearchSettingsViewModel = hiltViewModel()
    val filters by viewModel.filters.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val genres by viewModel.genres.collectAsState()

    // Локальное состояние для рейтинга; если фильтр не установлен, значение = null (то есть "Любой")
    var localRating by remember { mutableStateOf(filters.rating) }

    val countryText = filters.country?.let { id ->
        countries.find { it.id == id }?.name ?: "Неизвестная страна"
    } ?: "Любая страна"

    val genreText = filters.genre?.let { id ->
        genres.find { it.id == id }?.genre ?: "Неизвестный жанр"
    } ?: "Любой жанр"

    // Значения по умолчанию
    val defaultFilters = SearchFilters()
    // Формируем локальные фильтры с текущим значением рейтинга
    val localFilters = filters.copy(rating = localRating)
    // Флаг, указывающий, изменены ли настройки (учитывая также выбранный рейтинг)
    val isChanged = localFilters != defaultFilters

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки поиска", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Кнопка "Сбросить настройки" – активна, если хоть одна настройка отличается от дефолтных
                if (isChanged) {
                    Button(
                        onClick = {
                            viewModel.updateFilters(defaultFilters)
                            // Сброс локального значения рейтинга тоже
                            localRating = defaultFilters.rating
                            InMemorySearchSettings.triggerSearch()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Сбросить настройки", color = Color.White)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.updateFilters(defaultFilters)
                            localRating = defaultFilters.rating
                            InMemorySearchSettings.triggerSearch()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Сбросить настройки", color = MaterialTheme.colorScheme.primary)
                    }
                }
                // Кнопка "Применить настройки" – закрывает экран настроек
                Button(
                    onClick = {
                        viewModel.updateFilters(filters.copy(rating = localRating))
                        InMemorySearchSettings.triggerSearch()
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Применить настройки")
                }
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Показывать", style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChipCell("Все", filters.showType == "Все", { viewModel.updateShowType("Все") }, Modifier.weight(1f))
                FilterChipCell("Фильмы", filters.showType == "Фильмы", { viewModel.updateShowType("Фильмы") }, Modifier.weight(1f))
                FilterChipCell("Сериалы", filters.showType == "Сериалы", { viewModel.updateShowType("Сериалы") }, Modifier.weight(1f))
            }

            SearchOptionCard("Страна", countryText) { navController.navigate("search_country") }
            SearchOptionCard("Жанр", genreText) { navController.navigate("search_genre") }
            SearchOptionCard("Год", filters.period.ifEmpty { "Любой год" }) { navController.navigate("search_period") }

            // Блок настройки рейтинга с точностью до одной десятой
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Рейтинг",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (localRating == null) "Любой" else String.format("%.1f", localRating),
                        modifier = Modifier.clickable { localRating = null },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                val sliderValue = localRating ?: 5f
                Slider(
                    value = sliderValue,
                    onValueChange = { newValue -> localRating = newValue },
                    valueRange = 1f..10f,
                    steps = 89,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Text("Сортировать", style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChipCell("Дата", filters.sortBy == "Дата", { viewModel.updateSortBy("Дата") }, Modifier.weight(1f))
                FilterChipCell("Популярность", filters.sortBy == "Популярность", { viewModel.updateSortBy("Популярность") }, Modifier.weight(1f))
                FilterChipCell("Рейтинг", filters.sortBy == "Рейтинг", { viewModel.updateSortBy("Рейтинг") }, Modifier.weight(1f))
            }

            // Кнопка "Не просмотрен" всегда видна.
            if (filters.notWatched) {
                Button(
                    onClick = { viewModel.toggleNotWatched() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Не просмотрен", color = Color.White)
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.toggleNotWatched() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Не просмотрен", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun FilterChipCell(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SearchOptionCard(label: String, value: String, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label)
            Spacer(modifier = Modifier.weight(1f))
            Text(value, color = MaterialTheme.colorScheme.primary)
        }
    }
}
