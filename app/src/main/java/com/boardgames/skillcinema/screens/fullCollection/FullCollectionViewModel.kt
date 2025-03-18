package com.boardgames.skillcinema.screens.fullCollection

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.screens.moviesDetails.MovieDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCollectionViewModel @Inject constructor(
    private val repository: FullCollectionRepository,
    private val movieDetailsRepository: MovieDetailsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val collectionTitle: String = savedStateHandle["collectionTitle"] ?: "Коллекция"
    val collectionType: String = savedStateHandle["collectionType"] ?: ""

    private val _movies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val movies: StateFlow<UiState<List<Movie>>> = _movies

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            try {
                val moviesList = repository.getMoviesForCollection(collectionType)
                val moviesWithGenres = moviesList.map { movie ->
                    if (movie.genres == null || movie.genres.isEmpty()) {
                        try {
                            val movieDetails =
                                movieDetailsRepository.getMovieDetails(movie.id)
                            movie.copy(genres = movieDetails.genres)
                        } catch (e: Exception) {
                            movie // Если запрос не удался, возвращаем фильм без изменений
                        }
                    } else {
                        movie
                    }
                }
                if (moviesWithGenres.isNotEmpty()) {
                    _movies.value = UiState.Success(moviesWithGenres)
                } else {
                    Log.e("FullCollectionViewModel", "Нет данных для $collectionTitle")
                    _movies.value = UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e("FullCollectionViewModel", "Ошибка: ${e.localizedMessage}")
                _movies.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            try {
                val similarMoviesResponse =
                    movieDetailsRepository.getSimilarMovies(movieId)
                val similarMoviesWithGenres =
                    similarMoviesResponse.movies.map { movie ->
                        if (movie.genres == null || movie.genres.isEmpty()) {
                            try {
                                val movieDetails =
                                    movieDetailsRepository.getMovieDetails(movie.id)
                                movie.copy(genres = movieDetails.genres)
                            } catch (e: Exception) {
                                movie // Если запрос не удался, возвращаем фильм без изменений
                            }
                        } else {
                            movie
                        }
                    }
                _movies.value = UiState.Success(similarMoviesWithGenres)
            } catch (e: Exception) {
                _movies.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }
}