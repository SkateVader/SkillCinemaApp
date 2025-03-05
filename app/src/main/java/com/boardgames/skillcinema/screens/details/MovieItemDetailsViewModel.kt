package com.boardgames.skillcinema.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieItemDetailsViewModel @Inject constructor(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    // Кэш рейтингов: filmId -> rating
    private val _ratings = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val ratings: StateFlow<Map<Int, Float>> = _ratings

    fun loadDetails(movieId: Int) {
        // Если для данного movieId рейтинг уже загружен, ничего не делаем
        if (_ratings.value.containsKey(movieId)) return

        viewModelScope.launch {
            try {
                val detailsResponse = repository.getMovieDetails(movieId)
                val rating = detailsResponse.ratingKinopoisk
                if (rating != null && rating > 0f) {
                    _ratings.value = _ratings.value.toMutableMap().apply { put(movieId, rating) }
                }
            } catch (e: Exception) {
                // Можно логировать ошибку
            }
        }
    }
}
