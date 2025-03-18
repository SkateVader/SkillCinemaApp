package com.boardgames.skillcinema.screens.filmography

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.search.SearchMovieItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmographyScreen(
    navController: NavController,
    actorId: Int
) {
    val viewModel: FilmographyViewModel = hiltViewModel()
    LaunchedEffect(actorId) {
        viewModel.loadFilmography(actorId)
    }

    val filmographyState by viewModel.filmographyState.collectAsState()
    val filteredFilms by viewModel.filteredFilms.collectAsState()
    val availableProfessions by viewModel.availableProfessions.collectAsState()
    val selectedProfession by viewModel.selectedProfession.collectAsState()
    val personSex by viewModel.personSex.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp), // Добавляем отступы слева и справа
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Фильмография",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center // Явно задаем выравнивание по центру
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // Добавляем Spacer для баланса
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableProfessions.forEach { professionItem ->
                    val displayName = when (professionItem.profession) {
                        "ACTOR" -> if (personSex == "FEMALE") "Актриса" else "Актёр"
                        "VOICE" -> if (personSex == "FEMALE") "Актриса дубляжа" else "Актёр дубляжа"
                        "HIMSELF" ->
                            if (personSex == "FEMALE")
                                "Актриса: играет саму себя" else "Актёр: играет самого себя"

                        else -> getProfessionDisplayName(professionItem.profession)
                    }
                    val text = "$displayName (${professionItem.count})"
                    if (professionItem.profession == selectedProfession) {
                        Button(
                            onClick = { viewModel.onFilterSelected(professionItem.profession) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(text)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.onFilterSelected(professionItem.profession) },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(text)
                        }
                    }
                }
            }

            when (filmographyState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = (filmographyState as UiState.Error).message
                                ?: "Неизвестная ошибка",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is UiState.Success<*> -> {
                    if (filteredFilms.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет фильмов для выбранной профессии",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                            items(filteredFilms) { film ->
                                SearchMovieItem(movie = film) { selectedMovie ->
                                    navController.navigate("details/${selectedMovie.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getProfessionDisplayName(professionKey: String): String {
    return when (professionKey) {
        "DIRECTOR" -> "Режиссёр"
        "WRITER" -> "Сценарист"
        "PRODUCER" -> "Продюсер"
        "COMPOSER" -> "Композитор"
        "EDITOR" -> "Монтажёр"
        "OPERATOR" -> "Оператор"
        "HRONO_TITR_MALE" -> "Техническая роль"
        else -> professionKey
    }
}