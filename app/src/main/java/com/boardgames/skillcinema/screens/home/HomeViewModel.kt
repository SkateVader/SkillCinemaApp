package com.boardgames.skillcinema.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _topMovies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val topMovies: StateFlow<UiState<List<Movie>>> = _topMovies

    private val _popularMovies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val popularMovies: StateFlow<UiState<List<Movie>>> = _popularMovies

    private val _premieres = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val premieres: StateFlow<UiState<List<Movie>>> = _premieres

    private val _actionMovies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val actionMovies: StateFlow<UiState<List<Movie>>> = _actionMovies

    private val _dramaMovies = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val dramaMovies: StateFlow<UiState<List<Movie>>> = _dramaMovies

    private val _series = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val series: StateFlow<UiState<List<Movie>>> = _series

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded


    init {
        loadAllMovies()
    }

    private fun loadAllMovies() {
        viewModelScope.launch {
            val jobs = listOf(
                async { loadTopMovies() },
                async { loadPopularMovies() },
                async { loadPremieres() },
                async { loadActionMovies() },
                async { loadDramaMovies() },
                async { loadSeries() },
                async { delay(1000) } // Минимальное время загрузки 1 секунда
            )
            jobs.awaitAll()
            _isDataLoaded.value = true
        }
    }

    private fun processMovies(
        movies: List<Movie>,
        forSeries: Boolean = false,
        forceHasMore: Boolean = false
    ): Pair<List<Movie>, Boolean> {
        // Фильтруем фильмы по заполненности, исключаем те, у которых id == 0,
        // и применяем дополнительное условие для сериалов/фильмов.
        val filtered = movies.filter { movie ->
            movie.isCompleteData() && movie.id != 0
                    &&
                    if (forSeries) movie.type == "TV_SERIES" else movie.type != "TV_SERIES"
        }
        val hasMore = if (forceHasMore) filtered.size >= 20 else filtered.size > 20
        val displayed = if (hasMore) filtered.take(20) else filtered
        return Pair(displayed, hasMore)
    }


    fun loadTopMovies() {
        viewModelScope.launch {
            try {
                val fullMovies =
                    repository.getTopMovies() // Полный список для Топ-250 (250 фильмов)
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _topMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Топ-250")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка загрузки Топ-250: ${e.localizedMessage}")
                _topMovies.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            try {
                val fullMovies =
                    repository.getPopularMovies() // Полный список для Популярного (100 фильмов)
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _popularMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Популярного")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(
                    "HomeViewModel",
                    "Ошибка загрузки Популярного: ${e.localizedMessage}"
                )
                _popularMovies.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadPremieres() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getPremieres()
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = false)
                _premieres.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Премьер")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка загрузки Премьер: ${e.localizedMessage}")
                _premieres.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadActionMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getActionMovies()
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = false)
                _actionMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Боевиков США")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(
                    "HomeViewModel",
                    "Ошибка загрузки Боевиков США: ${e.localizedMessage}"
                )
                _actionMovies.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadDramaMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getDramaMovies()
                // Устанавливаем forceHasMore = true для Драм, чтобы если их ровно 20 или больше — кнопка "Показать все" отображалась.
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _dramaMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Драм Франции")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e(
                    "HomeViewModel",
                    "Ошибка загрузки Драм Франции: ${e.localizedMessage}"
                )
                _dramaMovies.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }

    fun loadSeries() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getSeries()
                // Для категории "Сериалы" устанавливаем forceHasMore = true, чтобы, если список содержит 20 или более,
                // кнопка "Показать все" отображалась.
                val (displayed, hasMore) =
                    processMovies(fullMovies, forSeries = true, forceHasMore = true)
                _series.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else {
                    Log.e("HomeViewModel", "Нет данных для Сериалов")
                    UiState.Error("Во время обработки запроса произошла ошибка")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Ошибка загрузки Сериалов: ${e.localizedMessage}")
                _series.value = UiState.Error("Во время обработки запроса произошла ошибка")
            }
        }
    }
}
