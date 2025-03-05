package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val topMoviesState by viewModel.topMovies.collectAsState()
    val popularMoviesState by viewModel.popularMovies.collectAsState()
    val premieresState by viewModel.premieres.collectAsState()
    val actionMoviesState by viewModel.actionMovies.collectAsState()
    val dramaMoviesState by viewModel.dramaMovies.collectAsState()
    val seriesState by viewModel.series.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("SkillCinema") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(bottom = 20.dp)
        ) {
            MovieCarousel(
                title = "Топ-250",
                uiState = topMoviesState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Топ-250/TOP_250_BEST_FILMS")
                }
            )
            MovieCarousel(
                title = "Популярное",
                uiState = popularMoviesState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Популярное/TOP_100_POPULAR_FILMS")
                }
            )
            MovieCarousel(
                title = "Премьеры",
                uiState = premieresState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Премьеры/PREMIERES")
                }
            )
            MovieCarousel(
                title = "Боевики США",
                uiState = actionMoviesState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Боевики США/ACTION")
                }
            )
            MovieCarousel(
                title = "Драмы Франции",
                uiState = dramaMoviesState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Драмы Франции/DRAMA")
                }
            )
            MovieCarousel(
                title = "Сериалы",
                uiState = seriesState,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                },
                onViewAll = {
                    navController.navigate("fullCollection/Сериалы/TV_SERIES")
                }
            )
        }
    }
}



