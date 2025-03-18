package com.boardgames.skillcinema.screens.fullCollection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.home.MovieItem
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCollectionScreen(
    navController: NavController,
    collectionTitle: String,
    collectionType: String,
    movieId: Int = 0
) {
    if (collectionType in listOf("User", "Favorites", "Watchlist", "Watched", "Interested")) {
        val collectionsViewModel: CollectionsViewModel = hiltViewModel()
        val movies = when (collectionType) {
            "Favorites" -> collectionsViewModel.favorites.collectAsState().value
            "Watchlist" -> collectionsViewModel.watchlist.collectAsState().value
            "Watched" -> collectionsViewModel.watched.collectAsState().value
            "Interested"-> collectionsViewModel.interested.collectAsState().value
            else -> {
                // Для пользовательских коллекций ищем коллекцию по имени
                val userCollections by collectionsViewModel.userCollections.collectAsState()
                userCollections.firstOrNull { it.name == collectionTitle }?.movies ?: emptyList()
            }
        }
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
                                text = collectionTitle,
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
                    actions = { Spacer(modifier = Modifier.width(48.dp)) }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (movies.isEmpty()) {
                    Text(text = "Коллекция пуста", style = MaterialTheme.typography.bodyLarge)
                } else {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val cellWidth = 120.dp
                        val x = (maxWidth - cellWidth * 2) / 3
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = x,
                                end = x,
                                top = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 16.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(x),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(movies) { movie ->
                                MovieItem(movie = movie, onMovieClick = {
                                    navController.navigate("details/${movie.id}")
                                })
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Стандартная логика для других подборок через FullCollectionViewModel
        val viewModel: FullCollectionViewModel = hiltViewModel()
        val moviesState by viewModel.movies.collectAsState()

        LaunchedEffect(collectionType, movieId) {
            if (collectionType == "SIMILAR") {
                viewModel.loadSimilarMovies(movieId)
            } else {
                viewModel.loadMovies()
            }
        }

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
                                text = collectionTitle,
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
                    actions = { Spacer(modifier = Modifier.width(48.dp)) }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (moviesState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = (moviesState as UiState.Error).message,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    is UiState.Success<*> -> {
                        val movies = (moviesState as UiState.Success<List<Movie>>).data
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val cellWidth = 120.dp
                            val x = (maxWidth - cellWidth * 2) / 3
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(
                                    start = x,
                                    end = x,
                                    top = 16.dp,
                                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(x),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(movies) { movie ->
                                    MovieItem(movie = movie, onMovieClick = {
                                        navController.navigate("details/${movie.id}")
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

