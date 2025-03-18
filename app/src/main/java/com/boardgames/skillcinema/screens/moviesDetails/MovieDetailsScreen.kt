package com.boardgames.skillcinema.screens.moviesDetails

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.boardgames.skillcinema.R
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel
import com.boardgames.skillcinema.screens.home.MovieItem
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.MovieDetailsResponse
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.addToCollection.AddToCollectionOverlay
import com.boardgames.skillcinema.screens.gallery.GallerySection
import com.boardgames.skillcinema.screens.home.ViewAllButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navController: NavController,
    movieId: Int,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    collectionsViewModel: CollectionsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val movieDetailsState by viewModel.movieDetails.collectAsState()
    val cast by viewModel.cast.collectAsState()
    val crew by viewModel.crew.collectAsState()
    val galleryState by viewModel.gallery.collectAsState()
    val similarMovies by viewModel.similarMovies.collectAsState()
    val similarMoviesTotal by viewModel.similarMoviesTotal.collectAsState()

    var showCollectionOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    movieDetailsState.let { state ->
        if (state is UiState.Success) {
            val details = state.data as MovieDetailsResponse
            val movieForCollection = Movie(
                id = movieId,
                imageUrl = details.posterUrl,
                title = details.title,
                ratingKinopoisk = details.ratingKinopoisk ?: 0f,
                year = details.year,
                genres = details.genres
            )
            LaunchedEffect(details) {
                collectionsViewModel.addToInterested(movieForCollection)
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                when (movieDetailsState) {
                    is UiState.Loading -> {
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
                        val details = (movieDetailsState as UiState.Success).data as MovieDetailsResponse
                        val isFilm = (details.type == "FILM")

                        val movieForCollection = Movie(
                            id = movieId,
                            imageUrl = details.posterUrl,
                            title = details.title,
                            ratingKinopoisk = details.ratingKinopoisk ?: 0f,
                            year = details.year,
                            genres = details.genres
                        )
                        val favoriteSelected = collectionsViewModel.favorites.collectAsState().value.any { it.id == movieId }
                        val watchlistSelected = collectionsViewModel.watchlist.collectAsState().value.any { it.id == movieId }
                        val watchedSelected = collectionsViewModel.watched.collectAsState().value.any { it.id == movieId }

                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            ) {
                                AsyncImage(
                                    model = details.posterUrl ?: R.drawable.ic_problem,
                                    contentDescription = details.title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .blur(6.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                                            )
                                        )
                                        .defaultMinSize(minHeight = 150.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = (if (!details.originalTitle.isNullOrEmpty()) details.originalTitle else details.title).uppercase(),
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = Color.White,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = ("${String.format("%.1f", details.ratingKinopoisk ?: 0f)}, "),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.White
                                            )
                                            Text(
                                                text = details.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = ("${details.year}, "),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.White
                                            )
                                            val genresText = details.genres.joinToString { it.genre }
                                            Text(
                                                text = genresText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = ("${details.country ?: ""}, "),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                            val totalMinutes = details.filmLength ?: 0
                                            val hours = totalMinutes / 60
                                            val minutes = totalMinutes % 60
                                            val durationText =
                                                if (hours > 0) "$hours ч $minutes мин" else "$minutes мин"
                                            Text(
                                                text = durationText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = details.ratingAgeLimits?.let {
                                                    it.replace("age", "").trim().let { cleaned ->
                                                        if (cleaned.isNotEmpty()) "$cleaned+" else ""
                                                    }
                                                } ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { collectionsViewModel.toggleFavorite(movieForCollection) },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (favoriteSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                    contentDescription = "Избранное",
                                                    tint = if (favoriteSelected) Color.Yellow else Color.White
                                                )
                                            }
                                            IconButton(
                                                onClick = { collectionsViewModel.toggleWatchlist(movieForCollection) },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Bookmark,
                                                    contentDescription = "Хочу посмотреть",
                                                    tint = if (watchlistSelected) Color.Yellow else Color.White
                                                )
                                            }
                                            IconButton(
                                                onClick = { collectionsViewModel.toggleWatched(movieForCollection) },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (watchedSelected) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                    contentDescription = "Просмотрено",
                                                    tint = if (watchedSelected) Color.Yellow else Color.White
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    val shareIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, "https://www.imdb.com/title/${details.imdbId}/")
                                                        type = "text/plain"
                                                    }
                                                    context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
                                                },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Share,
                                                    contentDescription = "Поделиться",
                                                    tint = Color.White
                                                )
                                            }
                                            IconButton(
                                                onClick = { showCollectionOverlay = true },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.MoreVert,
                                                    contentDescription = "Добавить в коллекцию",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            var expanded by remember { mutableStateOf(false) }
                            val desc = details.description ?: ""
                            val displayDesc = if (!expanded && desc.length > 250) desc.take(250) + "..." else desc
                            Text(
                                text = displayDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            if (!isFilm) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Сезоны и серии", style = MaterialTheme.typography.titleMedium)
                                    TextButton(onClick = {
                                        navController.navigate("seriesEpisodes/${movieId}/${details.title}/${details.seasonsCount}")
                                    }) {
                                        Text("Все", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val seasonsCount = details.seasonsCount ?: 0
                                val episodesCount = details.episodesCount ?: 0
                                Text(
                                    text = "$seasonsCount сезонов, $episodesCount серий",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            val filteredActors = cast.filter {
                                !it.profession.isNullOrBlank() && it.profession.lowercase() in listOf("actor")
                            }
                            if (filteredActors.isNotEmpty()) {
                                ActorsGridSection(
                                    cast = filteredActors,
                                    sectionTitle = if (isFilm) "В фильме снимались" else "В сериале снимались",
                                    onActorClick = { actor ->
                                        navController.navigate("personDetails/${actor.staffId}")
                                    },
                                    onMoreClick = {
                                        navController.navigate("fullActors/${movieId}")
                                    }
                                )
                            }
                            val filteredCrew = crew.filter {
                                !it.profession.isNullOrBlank() && it.profession.lowercase() !in listOf("actor")
                            }
                            if (filteredCrew.isNotEmpty()) {
                                CrewGridSection(
                                    crew = filteredCrew,
                                    sectionTitle = if (isFilm) "Над фильмом работали" else "Над сериалом работали",
                                    onCrewClick = { crewMember ->
                                        navController.navigate("personDetails/${crewMember.staffId}")
                                    },
                                    onMoreClick = {
                                        navController.navigate("fullCrew/${movieId}")
                                    }
                                )
                            }
                            val galleryResponse = galleryState
                            if (galleryResponse != null && galleryResponse.total > 0) {
                                GallerySection(
                                    total = galleryResponse.total,
                                    images = galleryResponse.images,
                                    movieId = movieId,
                                    navController = navController
                                )
                            }
                            if (similarMovies.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Похожие фильмы",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    TextButton(onClick = {
                                        navController.navigate("fullCollection/Похожие фильмы/SIMILAR/$movieId")
                                    }) {
                                        Text(
                                            "$similarMoviesTotal >",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    val displayMovies = if (similarMovies.size >= 20) similarMovies.take(20) else similarMovies
                                    items(displayMovies) { movie ->
                                        MovieItem(movie = movie) {
                                            navController.navigate("details/${movie.id}")
                                        }
                                    }
                                    if (similarMovies.size > 20) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .height(180.dp) // Высота постера
                                                    .fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                ViewAllButton(onViewAll = {
                                                    navController.navigate("fullCollection/Похожие фильмы/SIMILAR/$movieId")
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
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.align(Alignment.TopCenter)
            )
            if (showCollectionOverlay) {
                AddToCollectionOverlay(
                    movie = movieDetailsState.let {
                        if (it is UiState.Success<*>) (it.data as MovieDetailsResponse).let { details ->
                            Movie(
                                id = movieId,
                                imageUrl = details.posterUrl,
                                title = details.title,
                                ratingKinopoisk = details.ratingKinopoisk ?: 0f,
                                year = details.year,
                                genres = details.genres
                            )
                        } else null
                    },
                    onDismiss = { showCollectionOverlay = false },
                    collectionsViewModel = collectionsViewModel // Передаем viewModel явно
                )
            }
        }
    }
}