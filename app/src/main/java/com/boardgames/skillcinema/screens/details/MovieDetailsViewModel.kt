package com.boardgames.skillcinema.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.MovieDetailsResponse
import com.boardgames.skillcinema.data.remote.CastMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<UiState<MovieDetailsResponse>>(UiState.Loading)
    val movieDetails: StateFlow<UiState<MovieDetailsResponse>> = _movieDetails

    private val _cast = MutableStateFlow<List<CastMember>>(emptyList())
    val cast: StateFlow<List<CastMember>> = _cast

    private val _gallery = MutableStateFlow<List<com.boardgames.skillcinema.data.remote.GalleryItem>>(emptyList())
    val gallery: StateFlow<List<com.boardgames.skillcinema.data.remote.GalleryItem>> = _gallery

    private val _similarMovies = MutableStateFlow<List<com.boardgames.skillcinema.data.remote.Movie>>(emptyList())
    val similarMovies: StateFlow<List<com.boardgames.skillcinema.data.remote.Movie>> = _similarMovies

    fun loadMovieDetails(movieId: Int) {
        if (movieId <= 0) {
            _movieDetails.value = UiState.Error("Invalid film id: $movieId")
            return
        }
        viewModelScope.launch {
            try {
                val details = repository.getMovieDetails(movieId)
                _movieDetails.value = UiState.Success(details)
                _cast.value = repository.getMovieCast(movieId)
                _gallery.value = repository.getMovieGallery(movieId).images
                _similarMovies.value = repository.getSimilarMovies(movieId).movies
            } catch (e: Exception) {
                _movieDetails.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }

}
