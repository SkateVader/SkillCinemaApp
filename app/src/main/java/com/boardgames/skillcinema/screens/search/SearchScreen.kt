package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.ErrorOverlay
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel

@Composable
fun SearchScreen(navController: NavController) {
    val searchViewModel: SearchViewModel = hiltViewModel()
    val settingsViewModel: SearchSettingsViewModel = hiltViewModel()
    // Получаем результаты поиска и настройки
    val searchResults by searchViewModel.searchResults.collectAsState()
    val personResults by searchViewModel.personResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val errorMessage by searchViewModel.errorMessage.collectAsState()
    val filters by settingsViewModel.filters.collectAsState()
    val searchTrigger by InMemorySearchSettings.searchTriggerFlow.collectAsState()

    // Получаем список просмотренных фильмов
    val collectionsViewModel: CollectionsViewModel = hiltViewModel()
    val watchedList by collectionsViewModel.watched.collectAsState()

    // Сохраняем поисковый запрос при навигации
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Настройки по умолчанию для сравнения
    val defaultFilters = SearchFilters()
    // Подсвечиваем значок настроек, если настройки изменены
    val settingsIconTint =
        if (filters != defaultFilters) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface

    // Запускаем поиск при изменении запроса, настроек или триггера
    LaunchedEffect(searchQuery, filters, searchTrigger) {
        if (searchQuery.isNotBlank()) {
            searchViewModel.searchMovies(searchQuery, filters)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Поиск") },
                    trailingIcon = {
                        IconButton(onClick = { navController.navigate("search_settings") }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Настройки поиска",
                                tint = settingsIconTint
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Фильтруем фильмы по условию "не просмотрен"
                    val filteredMovies = if (filters.notWatched) {
                        searchResults?.movies.orEmpty().filter { movie ->
                            !watchedList.any { it.id == movie.id }
                        }
                    } else {
                        searchResults?.movies.orEmpty()
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        // Если выбраны "Фильмы" или "Сериалы", показываем только фильмы/сериалы
                        if ((filters.showType == "Фильмы" || filters.showType == "Сериалы") &&
                            filteredMovies.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Фильмы и сериалы",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(filteredMovies) { movie ->
                                SearchMovieItem(movie) { selectedMovie ->
                                    navController.navigate("details/${selectedMovie.id}")
                                }
                            }
                        }
                        // Если выбран "Все", показываем сначала фильмы/сериалы, затем персоны
                        if (filters.showType == "Все") {
                            if (filteredMovies.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Фильмы и сериалы",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(filteredMovies) { movie ->
                                    SearchMovieItem(movie) { selectedMovie ->
                                        navController.navigate("details/${selectedMovie.id}")
                                    }
                                }
                            }
                            if (personResults.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Люди",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(personResults) { person ->
                                    SearchPersonItem(person) { selectedPerson ->
                                        navController.navigate("personDetails/${
                                            selectedPerson.personId}")
                                    }
                                }
                            }
                        }
                        if (filteredMovies.isEmpty() && personResults.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment =
                                Alignment.Center) {
                                    Text("Ничего не найдено")
                                }
                            }
                        }
                    }
                }
            }
            errorMessage?.let { error ->
                ErrorOverlay(errorMessage = error, onDismiss = { searchViewModel.clearError() })
            }
        }
    }
}
