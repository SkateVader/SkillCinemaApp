package com.boardgames.skillcinema.screens.collections

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    navController: NavController,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val watched by viewModel.watched.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Коллекции") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            CollectionSection("Любимые", favorites) { movie ->
                navController.navigate("details/${movie.id}")
            }
            if (favorites.isEmpty()) {
                Text(text = "Список пуст", modifier = Modifier.padding(8.dp))
            }
            CollectionSection("Хочу посмотреть", watchlist) { movie ->
                navController.navigate("details/${movie.id}")
            }
            if (watchlist.isEmpty()) {
                Text(text = "Список пуст", modifier = Modifier.padding(8.dp))
            }
            CollectionSection("Уже просмотрено", watched) { movie ->
                navController.navigate("details/${movie.id}")
            }
            if (watched.isEmpty()) {
                Text(text = "Список пуст", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun CollectionSection(title: String, movies: List<Movie>, onItemClick: (Movie) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(movies) { movie ->
                CollectionItem(movie = movie, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
fun CollectionItem(movie: Movie, onItemClick: (Movie) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(movie) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(movie.imageUrl ?: ""),
            contentDescription = movie.title ?: "Нет названия",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(movie.title ?: "Названия нет", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
