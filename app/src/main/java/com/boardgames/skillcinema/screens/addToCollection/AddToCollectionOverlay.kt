package com.boardgames.skillcinema.screens.addToCollection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.boardgames.skillcinema.R
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.screens.collections.CollectionsViewModel
import com.boardgames.skillcinema.screens.collections.UserCollection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionOverlay(
    movie: Movie?,
    onDismiss: () -> Unit,
    collectionsViewModel: CollectionsViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()

    // Получаем списки стандартных коллекций
    val favoritesList by collectionsViewModel.favorites.collectAsState()
    val watchlistList by collectionsViewModel.watchlist.collectAsState()
    val initialFavorite = movie != null && favoritesList.any { it.id == movie.id }
    val initialWatchlist = movie != null && watchlistList.any { it.id == movie.id }
    var addToFavorite by remember { mutableStateOf(initialFavorite) }
    var addToWatchlist by remember { mutableStateOf(initialWatchlist) }

    // Получаем список просмотренных для отображения иконки "просмотрено"
    val watchedList by collectionsViewModel.watched.collectAsState()
    val isWatched = movie != null && watchedList.any { it.id == movie.id }

    // Пользовательские коллекции
    val userCollections by collectionsViewModel.userCollections.collectAsState()
    val selectedUserCollections = remember(movie, userCollections) {
        mutableStateListOf<String>().apply {
            movie?.let { m ->
                addAll(userCollections.filter { it.movies.any { movieInCol ->
                    movieInCol.id == m.id } }
                    .map { it.name })
            }
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp) // Отступ для BottomNavigationBar
        ) {
            item {
                // Заголовок с кнопкой закрытия
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Закрыть",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }
            }
            item {
                // Блок с постером и информацией о фильме
                if (movie != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(180.dp, 120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = movie.imageUrl,
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (isWatched) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_eye),
                                    contentDescription = "Просмотрено",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = movie.title ?: "Название отсутствует",
                                style = MaterialTheme.typography.titleMedium
                            )
                            val yearText = movie.year?.toString() ?: "Неизвестный год"
                            val genreText = movie.genre ?: "Неизвестный жанр"
                            Text(
                                text = "$yearText, $genreText",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            item {
                // Метка "Добавить в коллекцию:" с иконкой
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Добавить в коллекцию:")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(userCollections) { userCollection: UserCollection ->
                // Получаем количество фильмов в пользовательской коллекции
                val count = userCollection.movies.size
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedUserCollections.contains(userCollection.name),
                        onCheckedChange = { checked ->
                            if (checked)
                                selectedUserCollections.add(userCollection.name)
                            else
                                selectedUserCollections.remove(userCollection.name)
                            collectionsViewModel.toggleUserCollection(movie, userCollection.name, checked)
                        }
                    )
                    // Отображаем название коллекции с количеством фильмов в скобках
                    Text(text = "${userCollection.name} ($count)", modifier = Modifier.weight(1f))
                }
            }
            item {
                // Получаем количество фильмов в стандартных коллекциях
                val watchlistCount = watchlistList.size
                val favoritesCount = favoritesList.size
                // Чекбоксы для стандартных коллекций "Хочу посмотреть" и "Избранное"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = addToWatchlist,
                        onCheckedChange = { checked ->
                            addToWatchlist = checked
                            collectionsViewModel.toggleWatchlist(movie)
                        }
                    )
                    // Отображаем "Хочу посмотреть" с количеством фильмов в скобках
                    Text(text = "Хочу посмотреть ($watchlistCount)", modifier = Modifier.weight(1f))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = addToFavorite,
                        onCheckedChange = { checked ->
                            addToFavorite = checked
                            collectionsViewModel.toggleFavorite(movie)
                        }
                    )
                    // Отображаем "Избранное" с количеством фильмов в скобках
                    Text(text = "Избранное ($favoritesCount)", modifier = Modifier.weight(1f))
                }
            }
            item {
                // Кнопка создания новой пользовательской коллекции
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCreateDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Создать свою коллекцию")
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { collectionName ->
                collectionsViewModel.createUserCollection(collectionName)
                showCreateDialog = false
            }
        )
    }
}