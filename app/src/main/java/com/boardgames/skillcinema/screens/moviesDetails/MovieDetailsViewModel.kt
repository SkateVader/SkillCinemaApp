package com.boardgames.skillcinema.screens.moviesDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.CastResponse
import com.boardgames.skillcinema.data.remote.CrewResponse
import com.boardgames.skillcinema.data.remote.GalleryResponse
import com.boardgames.skillcinema.data.remote.MovieDetailsResponse
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

    private val _cast = MutableStateFlow<List<CastResponse>>(emptyList())
    val cast: StateFlow<List<CastResponse>> = _cast

    private val _crew = MutableStateFlow<List<CrewResponse>>(emptyList())
    val crew: StateFlow<List<CrewResponse>> = _crew

    private val _gallery = MutableStateFlow<GalleryResponse?>(null)
    val gallery: StateFlow<GalleryResponse?> = _gallery

    private val _similarMovies =
        MutableStateFlow<List<com.boardgames.skillcinema.data.remote.Movie>>(emptyList())
    val similarMovies: StateFlow<List<com.boardgames.skillcinema.data.remote.Movie>> =
        _similarMovies

    private val _similarMoviesTotal = MutableStateFlow(0)
    val similarMoviesTotal: StateFlow<Int> = _similarMoviesTotal

    fun loadMovieDetails(movieId: Int) {
        if (movieId <= 0) {
            _movieDetails.value = UiState.Error("Invalid film id: $movieId")
            return
        }
        viewModelScope.launch {
            try {
                val details = repository.getMovieDetails(movieId)
                if (details.type != "FILM") {
                    val seasonsResponse =
                        repository.getSeriesEpisodes(movieId)
                    val seasonsCount = seasonsResponse.seasons?.size ?: 0
                    val episodesCount = seasonsResponse.seasons?.sumOf {
                        it.episodes?.size ?: 0
                    } ?: 0
                    val updatedDetails = details.copy(
                        seasonsCount = seasonsCount,
                        episodesCount = episodesCount
                    )
                    _movieDetails.value = UiState.Success(updatedDetails)
                } else {
                    _movieDetails.value = UiState.Success(details)
                }
                _cast.value = repository.getMovieCast(movieId)
                _crew.value = repository.getMovieCrew(movieId)
                _gallery.value = repository.getMovieGallery(movieId)

                // Загрузка похожих фильмов с жанрами и общего количества
                val similarMoviesResponse =
                    repository.getSimilarMovies(movieId)
                val similarMoviesWithGenres =
                    similarMoviesResponse.movies.map { movie ->
                        try {
                            val movieDetails = repository.getMovieDetails(movie.id)
                            Log.d(
                                "MovieDetails", "Genres for movie ${movie.id}:" +
                                        " ${movieDetails.genres}"
                            )
                            movie.copy(genres = movieDetails.genres) // Теперь работает корректно
                        } catch (e: Exception) {
                            Log.e(
                                "MovieDetails", "Error fetching details for movie " +
                                        "${movie.id}: ${e.localizedMessage}"
                            )
                            movie
                        }
                    }
                _similarMovies.value = similarMoviesWithGenres
                _similarMoviesTotal.value = similarMoviesResponse.movies.size
            } catch (e: Exception) {
                _movieDetails.value = UiState.Error("Ошибка: ${e.localizedMessage}")
            }
        }
    }
}