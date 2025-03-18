package com.boardgames.skillcinema.screens.fullImage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.boardgames.skillcinema.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullImageScreen(
    navController: NavController,
    movieId: Int,
    type: String, // Тип изображений (например, STILL, POSTER, FAN_ART и т.д.)
    initialImageUrl: String // URL изображения, которое нужно показать первым
) {
    // Если movieId равен 0, считаем, что это фото персоны – API не вызываем
    // , сразу отображаем изображение
    if (movieId == 0) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = initialImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
        return
    }

    // Для movieId != 0 вызываем API для загрузки изображений
    val viewModel: FullImageViewModel = hiltViewModel()
    val imagesState by viewModel.images.collectAsState()
    // Используем rememberPagerState, количество страниц определяется через lambda
    val pagerState = rememberPagerState {
        if (imagesState is UiState.Success) (imagesState as UiState.Success).data.size else 0
    }

    LaunchedEffect(movieId, type) {
        viewModel.loadImages(movieId, type)
    }

    // Устанавливаем начальный индекс, если найдено совпадение с initialImageUrl
    LaunchedEffect(imagesState) {
        if (imagesState is UiState.Success) {
            val images = (imagesState as UiState.Success).data
            val initialIndex = images.indexOfFirst { it.url == initialImageUrl }
            if (initialIndex >= 0) {
                pagerState.scrollToPage(initialIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Заголовок можно оставить пустым */ },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (imagesState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (imagesState as UiState.Error).message)
                }
            }
            is UiState.Success -> {
                val images = (imagesState as UiState.Success).data
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { page ->
                    AsyncImage(
                        model = images[page].url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
