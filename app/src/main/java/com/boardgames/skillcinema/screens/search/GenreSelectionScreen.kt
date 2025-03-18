package com.boardgames.skillcinema.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.boardgames.skillcinema.data.remote.Genre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelectionScreen(navController: NavController) {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("search_settings")
    }
    val viewModel: SearchSettingsViewModel = hiltViewModel(parentEntry)
    val genres by viewModel.genres.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Добавляем "Любой жанр" в начало списка
    val filteredGenres = listOf(Genre(genre = "Любой жанр", id = null)) +
            genres.filter {
                it.genre.contains(searchText, ignoreCase = true)
            }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Центрированный заголовок с отступами
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Жанр",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Поиск жанра") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(filteredGenres) { genre ->
                Text(
                    text = genre.genre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Если выбран "Любой жанр", сохраняем null, иначе – выбранный ID жанра
                            val selectedId = if (genre.genre == "Любой жанр")
                                null else genre.id
                            viewModel.updateGenre(selectedId)
                            navController.navigateUp()
                        }
                        .padding(16.dp)
                )
                Divider()
            }
        }
    }
}
