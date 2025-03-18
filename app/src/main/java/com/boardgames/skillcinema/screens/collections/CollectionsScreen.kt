package com.boardgames.skillcinema.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.R
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.addToCollection.CreateCollectionDialog
import com.boardgames.skillcinema.screens.home.MovieItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    navController: NavController,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val watched by viewModel.watched.collectAsState()
    val userCollections by viewModel.userCollections.collectAsState()
    val interestedList by viewModel.interested.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Коллекции") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        // Общий скролл по всему экрану
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Блок "Просмотрено"
            if (watched.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Просмотрено", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${watched.size} >",
                        modifier = Modifier.clickable {
                            navController.navigate("fullCollection/Просмотрено/Watched/0")
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val watchedItems = if (watched.size > 20) watched.take(20)
                    else watched
                    items<Movie>(watchedItems) { movie ->
                        MovieItem(movie = movie) {
                            navController.navigate("details/${movie.id}")
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(180.dp)
                                .clickable { viewModel.clearWatched() },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "Очистить историю",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Очистить\nисторию",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            // Блок "Коллекции" (стандартные коллекции всегда отображаются)
            Text(text = "Коллекции", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showCreateCollectionDialog.value = true }
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
            if (viewModel.showCreateCollectionDialog.value) {
                CreateCollectionDialog(
                    onDismiss = { viewModel.showCreateCollectionDialog.value = false },
                    onCreate = { name ->
                        viewModel.createUserCollection(name)
                        viewModel.showCreateCollectionDialog.value = false
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Грид стандартных и пользовательских коллекций.
            // Стандартные коллекции favorites и watchlist всегда отображаются, даже если пустые.
            val standardCount = 2 // всегда 2 элемента
            val totalItems = standardCount + userCollections.size
            // Вычисляем число строк (2 элемента в строке)
            val rows = (totalItems + 1) / 2
            val cellHeight: Dp = 184.dp
            val verticalSpacing: Dp = 16.dp
            val gridHeight = cellHeight * rows + verticalSpacing * (rows - 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Стандартные коллекции
                    item {
                        CollectionGridItem(
                            collectionName = "Любимое",
                            movieCount = favorites.size,
                            iconRes = R.drawable.ic_favorite,
                            onDelete = { /* Не удаляется */ },
                            onClick = {
                                navController.navigate("fullCollection/Любимое/Favorites/0")
                            }
                        )
                    }
                    item {
                        CollectionGridItem(
                            collectionName = "Хочу посмотреть",
                            movieCount = watchlist.size,
                            iconRes = R.drawable.ic_watchlist,
                            onDelete = { /* Не удаляется */ },
                            onClick = {
                                navController.navigate(
                                    "fullCollection/Хочу посмотреть/Watchlist/0"
                                )
                            }
                        )
                    }
                    // Пользовательские коллекции – выводятся только если не пусты
                    if (userCollections.isNotEmpty()) {
                        items(userCollections) { collection ->
                            CollectionGridItem(
                                collectionName = collection.name,
                                movieCount = collection.movies.size,
                                iconRes = R.drawable.ic_person,
                                onDelete = { viewModel.deleteUserCollection(collection) },
                                onClick = {
                                    navController.navigate(
                                        "fullCollection/${collection.name}/User/0"
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Блок "Вам было интересно"
            if (interestedList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Вам было интересно", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${interestedList.size} >",
                        modifier = Modifier.clickable {
                            navController.navigate(
                                "fullCollection/Вам было интересно/Interested/0"
                            )
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val interestedItems =
                        if (interestedList.size > 20) interestedList.take(20) else interestedList
                    items<Movie>(interestedItems) { movie ->
                        MovieItem(movie = movie) {
                            navController.navigate("details/${movie.id}")
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(180.dp)
                                .clickable { viewModel.clearInterested() },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "Очистить историю",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Очистить\nисторию",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CollectionGridItem(
    collectionName: String,
    movieCount: Int,
    iconRes: Int,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            // Прямоугольная форма (например, 200dp на 200dp),
            // можно изменить по необходимости
            .width(200.dp)
            .height(184.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        // Кнопка удаления в правом верхнем углу
        if (collectionName !in listOf("Любимое", "Хочу посмотреть")) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Удалить",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .clickable { onDelete() }
            )
        }
        // Основной контент по центру
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp), // отступы сверху/снизу
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Иконка коллекции
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = collectionName,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Название коллекции
            Text(
                text = collectionName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Счётчик фильмов (бейдж)
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$movieCount",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}






