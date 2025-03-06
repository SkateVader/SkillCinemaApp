package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchScreen(navController: NavController) {
    val searchViewModel: SearchViewModel = hiltViewModel()
    val settingsViewModel: SearchSettingsViewModel = hiltViewModel()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val filters by settingsViewModel.filters.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    // Собираем значение триггера, чтобы при его изменении повторно выполнять поиск
    val searchTrigger by InMemorySearchSettings.searchTriggerFlow.collectAsState()

    // Реакция на изменения поискового запроса (с debounce)
    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(500L)
            .distinctUntilChanged()
            .collectLatest { query ->
                if (query.isNotBlank()) {
                    searchViewModel.searchMovies(query, filters) { errorCode ->
                        // Обработка ошибок, если необходимо
                    }
                }
            }
    }

    // Реакция на изменение триггера (например, после нажатия "Применить настройки")
    LaunchedEffect(searchTrigger) {
        if (searchViewModel.lastSearchQuery.isNotBlank()) {
            searchViewModel.searchMovies(searchViewModel.lastSearchQuery, filters) { errorCode ->
                // Обработка ошибок, если это необходимо
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Поле ввода для поиска
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Поиск") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Divider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            IconButton(
                                onClick = { navController.navigate("search_settings") },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (filters != SearchFilters()) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Настройки поиска",
                                    tint = if (filters != SearchFilters()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение состояния загрузки и результатов поиска
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (searchResults?.items.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Фильмы или сериалы не найдены")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(searchResults?.items.orEmpty()) { movie ->
                        SearchMovieItem(movie) { selectedMovie ->
                            navController.navigate("details/${selectedMovie.id}")
                        }
                    }
                }
            }
        }
    }
}


