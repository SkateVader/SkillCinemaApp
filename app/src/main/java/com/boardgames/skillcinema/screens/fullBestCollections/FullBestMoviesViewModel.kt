package com.boardgames.skillcinema.screens.fullBestCollections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullBestMoviesViewModel @Inject constructor(
    private val repository: FullBestMoviesRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val movies: StateFlow<UiState<List<Movie>>> = _movies

    fun loadFullBestMovies(staffId: Int) {
        viewModelScope.launch {
            _movies.value = UiState.Loading
            try {
                val moviesList = repository.getFullBestMovies(staffId)
                _movies.value = UiState.Success(moviesList)
            } catch (e: Exception) {
                _movies.value = UiState.Error("Ошибка загрузки фильмов")
            }
        }
    }
}

