package com.boardgames.skillcinema.screens.fullActors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.CastResponse
import com.boardgames.skillcinema.navigation.BottomNavigationBar
import com.boardgames.skillcinema.screens.ErrorOverlay
import com.boardgames.skillcinema.screens.moviesDetails.ActorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullActorsScreen(
    navController: NavController,
    movieId: Int,
    viewModel: FullActorsViewModel = hiltViewModel()
) {
    val actorsState by viewModel.actors.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.fetchActors(movieId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Актерский состав") },
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
            when (actorsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    val cast =
                        (actorsState as UiState.Success<List<CastResponse>>).data
                            .distinctBy { it.staffId } // Убираем дубли по staffId

                    if (cast.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(cast.filter {
                                !it.profession.isNullOrBlank()
                                        && it.profession.lowercase() in listOf("actor")
                            }, key = { it.staffId }) { actor ->
                                ActorItem(actor = actor, onClick = {
                                    navController.navigate("actorDetails/${actor.staffId}")
                                })
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Актеры не найдены")
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorOverlay(
                        errorMessage = (actorsState as UiState.Error).message,
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
