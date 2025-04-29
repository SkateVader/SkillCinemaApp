package com.boardgames.skillcinema.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.ErrorOverlay
import com.boardgames.skillcinema.UiState

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

    // Агрегируем сообщение об ошибке (берём первое непустое)
    val errorMessage = remember {
        derivedStateOf {
            listOf(
                (topMoviesState as? UiState.Error)?.message,
                (popularMoviesState as? UiState.Error)?.message,
                (premieresState as? UiState.Error)?.message,
                (actionMoviesState as? UiState.Error)?.message,
                (dramaMoviesState as? UiState.Error)?.message,
                (seriesState as? UiState.Error)?.message
            ).firstOrNull { it != null }
        }
    }.value

    // Локальное состояние для управления видимостью оверлея
    var showErrorOverlay by remember { mutableStateOf(false) }

    // Обновляем видимость оверлея при изменении errorMessage
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showErrorOverlay = true
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("SkillCinema") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(bottom = 20.dp)
            ) {
                MovieCarousel(
                    title = "Топ-250",
                    uiState = topMoviesState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Топ-250/TOP_250_BEST_FILMS/0") }
                )
                MovieCarousel(
                    title = "Популярное",
                    uiState = popularMoviesState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Популярное/TOP_100_POPULAR_FILMS/0") }
                )
                MovieCarousel(
                    title = "Премьеры",
                    uiState = premieresState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Премьеры/PREMIERES/0") }
                )
                MovieCarousel(
                    title = "Боевики США",
                    uiState = actionMoviesState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Боевики США/ACTION/0") }
                )
                MovieCarousel(
                    title = "Драмы Франции",
                    uiState = dramaMoviesState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Драмы Франции/DRAMA/0") }
                )
                MovieCarousel(
                    title = "Сериалы",
                    uiState = seriesState,
                    onMovieClick = { movie -> navController.navigate("details/${movie.id}") },
                    onViewAll = { navController.navigate("" +
                            "fullCollection/Сериалы/TV_SERIES/0") }
                )
            }
            if (showErrorOverlay && errorMessage != null) {
                ErrorOverlay(
                    errorMessage = errorMessage,
                    onDismiss = {
                        showErrorOverlay = false
                        // Ошибка не сбрасывается, и оверлей может появиться снова при следующем обновлении
                    }
                )
            }
        }
    }
}