package com.boardgames.skillcinema.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.MovieResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: KinopoiskApi
) : ViewModel() {

    // Сохраняем последний запрос
    var lastSearchQuery by mutableStateOf("")
        private set

    private val _searchResults = MutableStateFlow<MovieResponse?>(null)
    val searchResults: StateFlow<MovieResponse?> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchMovies(keyword: String, filters: SearchFilters, onError: (Int) -> Unit) {
        // Обновляем сохранённый запрос
        lastSearchQuery = keyword
        viewModelScope.launch {
            if (keyword.isBlank()) {
                Log.e("SearchViewModel", "Поисковый запрос пуст!")
                return@launch
            }

            _isLoading.value = true
            _searchResults.value = null

            try {
                // Преобразование фильтра показа в параметр API
                val typeParam = when (filters.showType) {
                    "Фильмы" -> "FILM"
                    "Сериалы" -> "TV_SHOW"
                    else -> null  // "Все" – не передаём тип
                }

                val order = when (filters.sortBy) {
                    "Дата" -> "YEAR"
                    "Популярность" -> "NUM_VOTE"
                    "Рейтинг" -> "RATING"
                    else -> "NUM_VOTE"
                }

                var yearFrom: Int? = null
                var yearTo: Int? = null
                if (filters.period != "Любой год" && filters.period.contains(" - ")) {
                    val parts = filters.period.split(" - ")
                    yearFrom = parts.getOrNull(0)?.toIntOrNull()
                    yearTo = parts.getOrNull(1)?.toIntOrNull()
                }

                // Новый блок для рейтинга: если rating установлен, передаём его округлённое значение
                val ratingFrom: Int? = filters.rating?.toInt()
                val ratingTo: Int? = if (filters.rating != null) 10 else null

                val response = api.searchMovies(
                    keyword = keyword,
                    type = typeParam,
                    countries = filters.country?.toString(),
                    genres = filters.genre?.toString(),
                    yearFrom = yearFrom,
                    yearTo = yearTo,
                    ratingFrom = ratingFrom,
                    ratingTo = ratingTo,
                    order = order,
                    page = 1
                )

                _searchResults.value = response.copy(items = response.items.orEmpty())

            } catch (e: HttpException) {
                Log.e("SearchViewModel", "Ошибка HTTP: ${e.code()} - ${e.message()}")
                if (e.code() == 402) {
                    onError(402)
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Ошибка запроса: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
