package com.boardgames.skillcinema.screens.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieImagesScreen(
    navController: NavController,
    movieId: Int
) {
    val viewModel: MovieImagesViewModel = hiltViewModel()

    // Список типов изображений: пара "код" – "название"
    val imageTypes = listOf(
        "STILL" to "Кадры",
        "SHOOTING" to "Со съёмок",
        "POSTER" to "Постеры",
        "FAN_ART" to "Фан-арты",
        "PROMO" to "Промо",
        "CONCEPT" to "Концепт-арты",
        "WALLPAPER" to "Обои",
        "COVER" to "Обложки",
        "SCREENSHOT" to "Скриншоты"
    )

    // Состояние с количеством изображений для каждого типа
    val availableTypes by viewModel.availableTypes.collectAsState()

    var selectedType by remember { mutableStateOf("STILL") } // по умолчанию "Кадры"
    val imagesState by viewModel.images.collectAsState()

    // Загружаем доступные типы при запуске
    LaunchedEffect(movieId) {
        viewModel.loadAvailableTypes(movieId)
    }
    // Загружаем изображения выбранного типа
    LaunchedEffect(movieId, selectedType) {
        viewModel.loadImages(movieId, selectedType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Галерея", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = { Box(modifier = Modifier.width(48.dp)) }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Ряд фильтров, выровненный по центру горизонтально
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    imageTypes.forEach { (code, label) ->
                        // Отображаем кнопку только если для данного типа доступно
                        // хотя бы одно изображение
                        if ((availableTypes[code] ?: 0) > 0) {
                            FilterChipCell(
                                label = label,
                                isSelected = selectedType == code,
                                onClick = { selectedType = code },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
            when (imagesState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = (imagesState as UiState.Error).message,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is UiState.Success -> {
                    val images = (imagesState as UiState.Success).data
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(images) { image ->
                            AsyncImage(
                                model = image.url,
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable {
                                        // Передаём выбранный тип и URL нажатого изображения в маршрут
                                        navController.navigate(
                                            "fullScreenImage?movieId=${movieId}&type=" +
                                                    "${selectedType}&initialImageUrl=${image.url}"
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
