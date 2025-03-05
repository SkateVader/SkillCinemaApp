package com.boardgames.skillcinema.screens.details

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.boardgames.skillcinema.R
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel
import com.boardgames.skillcinema.screens.home.MovieItem
import com.boardgames.skillcinema.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navController: NavController,
    movieId: Int,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    // Получаем состояния из ViewModel
    val movieDetailsState by viewModel.movieDetails.collectAsState()
    val cast by viewModel.cast.collectAsState()
    val gallery by viewModel.gallery.collectAsState()
    val similarMovies by viewModel.similarMovies.collectAsState()
    val collectionsViewModel: CollectionsViewModel = hiltViewModel()
    val context = LocalContext.current

    // Загружаем данные при первом отображении экрана, если movieId корректный
    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фильма") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (movieDetailsState) {
                is UiState.Loading -> {
                    // Состояние загрузки
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Загрузка...")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadMovieDetails(movieId) }) {
                                Text("Повторить загрузку")
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    // Состояние ошибки
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = (movieDetailsState as UiState.Error).message)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadMovieDetails(movieId) }) {
                                Text("Повторить загрузку")
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    // Состояние успешной загрузки
                    val details = (movieDetailsState as UiState.Success).data
                    val movieForCollection = Movie(
                        id = movieId,
                        imageUrl = details.posterUrl,
                        title = details.title,
                        ratingKinopoisk = details.ratingKinopoisk ?: 0f,
                        year = details.year,
                        genres = details.genres
                    )
                    val favorites by collectionsViewModel.favorites.collectAsState()
                    val watchlist by collectionsViewModel.watchlist.collectAsState()
                    val watched by collectionsViewModel.watched.collectAsState()

                    val favoriteSelected = favorites.any { it.id == movieId }
                    val watchlistSelected = watchlist.any { it.id == movieId }
                    val watchedSelected = watched.any { it.id == movieId }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(details.posterUrl ?: R.drawable.ic_problem),
                            contentDescription = details.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = details.title, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = "Год: ${details.year}  •  Рейтинг: ${details.ratingKinopoisk ?: "Отсутствует"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Жанры: ${details.genres?.joinToString { it.genre } ?: "Информация отсутствует"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (details.seasonsCount != null) {
                            Text(
                                text = "Сезонов: ${details.seasonsCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        var expanded by remember { mutableStateOf(false) }
                        val desc = details.description ?: ""
                        val displayDesc = if (!expanded && desc.length > 250) desc.take(250) + "..." else desc
                        Text(
                            text = displayDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .padding(vertical = 8.dp)
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { collectionsViewModel.toggleFavorite(movieForCollection) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (favoriteSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("❤ Любимое")
                            }
                            Button(
                                onClick = { collectionsViewModel.toggleWatchlist(movieForCollection) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (watchlistSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("👀 Хочу посмотреть")
                            }
                            Button(
                                onClick = { collectionsViewModel.toggleWatched(movieForCollection) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (watchedSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Уже просмотрено")
                            }
                            Button(
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "https://www.imdb.com/title/${details.imdbId}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, null))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Поделиться")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (cast.isNotEmpty()) { CastSection(cast = cast) }
                        if (gallery.isNotEmpty()) { GallerySection(images = gallery) }
                        if (similarMovies.isNotEmpty()) {
                            Text(
                                "Похожие фильмы",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                similarMovies.forEach { movie ->
                                    MovieItem(movie = movie) { navController.navigate("details/${movie.id}") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
