package com.boardgames.skillcinema.screens.fullcollection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCollectionViewModel @Inject constructor(
    private val repository: FullCollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val collectionTitle: String = savedStateHandle["collectionTitle"] ?: "Коллекция"
    val collectionType: String = savedStateHandle["collectionType"] ?: ""

    private val _movies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val movies: StateFlow<UiState<List<Movie>>> = _movies

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val moviesList = repository.getMoviesForCollection(collectionType)
                if (moviesList.isNotEmpty()) {
                    _movies.value = UiState.Success(moviesList)
                } else {
                    _movies.value = UiState.Error("Нет данных для $collectionTitle")
                }
            } catch (e: Exception) {
                _movies.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }
}
