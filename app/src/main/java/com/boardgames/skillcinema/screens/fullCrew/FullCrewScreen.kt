package com.boardgames.skillcinema.screens.fullCrew

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.CrewResponse
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.ErrorOverlay
import com.boardgames.skillcinema.screens.moviesDetails.CrewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCrewScreen(
    navController: NavController,
    movieId: Int,
    viewModel: FullCrewViewModel = hiltViewModel()
) {
    val crewState by viewModel.crew.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.fetchCrew(movieId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Над фильмом работали") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (crewState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success<*> -> {
                    val crew =
                        (crewState as UiState.Success<List<CrewResponse>>).data
                            .distinctBy { it.staffId } // Убираем дубли по staffId

                    if (crew.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(crew.filter {
                                !it.profession.isNullOrBlank()
                                        && it.profession.lowercase() !in listOf("actor")
                            },
                                key = { it.staffId }) { member ->
                                CrewItem(crewMember = member, onClick = {
                                    navController.navigate("crewDetails/${member.staffId}")
                                })
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Команда фильма не найдена")
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorOverlay(
                        errorMessage = (crewState as UiState.Error).message,
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
