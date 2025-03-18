package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import com.boardgames.skillcinema.screens.ErrorOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsScreen(navController: NavController) {
    val viewModel: SearchSettingsViewModel = hiltViewModel()
    val filters by viewModel.filters.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var localRating by remember { mutableStateOf(filters.rating) }

    val countryText = filters.country?.let { id ->
        countries.find { it.id == id }?.name ?: "Неизвестная страна"
    } ?: "Любая страна"

    val genreText = filters.genre?.let { id ->
        genres.find { it.id == id }?.genre ?: "Неизвестный жанр"
    } ?: "Любой жанр"

    val defaultFilters = SearchFilters()
    val localFilters = filters.copy(rating = localRating)
    val isChanged = localFilters != defaultFilters

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Настройки поиска", style =
                        MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Показывать", style = MaterialTheme.typography.bodyLarge)
                SegmentedControl(
                    items = listOf("Все", "Фильмы", "Сериалы"),
                    selectedItem = filters.showType,
                    onItemSelected = { viewModel.updateShowType(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                SearchOptionCard("Страна", countryText) {
                    navController.navigate("search_country")
                }
                SearchOptionCard("Жанр", genreText) {
                    navController.navigate("search_genre")
                }
                SearchOptionCard("Год", filters.period.ifEmpty {
                    "Любой год"
                }) { navController.navigate("search_period") }

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
                            text = if (localRating == null) "Любой"
                            else String.format("%.1f", localRating),
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
                SegmentedControl(
                    items = listOf("Дата", "Популярность", "Рейтинг"),
                    selectedItem = filters.sortBy,
                    onItemSelected = { viewModel.updateSortBy(it) },
                    modifier = Modifier.fillMaxWidth()
                )

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

                if (isChanged) {
                    Button(
                        onClick = {
                            viewModel.updateFilters(defaultFilters)
                            localRating = defaultFilters.rating
                            InMemorySearchSettings.triggerSearch()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
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
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Сбросить настройки", color = MaterialTheme.colorScheme.primary)
                    }
                }

                Button(
                    onClick = {
                        viewModel.updateFilters(filters.copy(rating = localRating))
                        InMemorySearchSettings.triggerSearch()
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Применить настройки")
                }
            }

            errorMessage?.let { error ->
                ErrorOverlay(errorMessage = error, onDismiss = { viewModel.clearError() })
            }
        }
    }
}
