package com.boardgames.skillcinema.screens.fullcollection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCollectionScreen(
    navController: NavController,
    collectionTitle: String
) {
    val viewModel: FullCollectionViewModel = hiltViewModel()
    val moviesState by viewModel.movies.collectAsState()

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
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
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
                    // Используем BoxWithConstraints для расчета отступа
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val cellWidth = 120.dp
                        // Рассчитываем x так, чтобы: 2 * cellWidth + 3 * x = maxWidth
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
