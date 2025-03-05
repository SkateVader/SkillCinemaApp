package com.boardgames.skillcinema.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        loadAllMovies()
    }

    private fun loadAllMovies() {
        loadTopMovies()
        loadPopularMovies()
        loadPremieres()
        loadActionMovies()
        loadDramaMovies()
        loadSeries()
    }

    /**
     * Фильтрует список фильмов или сериалов:
     * - Если forSeries = true, оставляем только сериалы (movie.type == "TV_SERIES")
     * - Иначе – только фильмы (если поле type отсутствует или movie.type != "TV_SERIES")
     * Отбираются только элементы с заполненными данными (через movie.isCompleteData()).
     * Если forceHasMore установлен, то hasMore = true, если отфильтрованный список содержит 20 или более элементов.
     * В этом случае возвращается первые 20 элементов.
     */
    private fun processMovies(
        movies: List<Movie>,
        forSeries: Boolean = false,
        forceHasMore: Boolean = false
    ): Pair<List<Movie>, Boolean> {
        // Фильтруем фильмы по заполненности, исключаем те, у которых id == 0,
        // и применяем дополнительное условие для сериалов/фильмов.
        val filtered = movies.filter { movie ->
            movie.isCompleteData() && movie.id != 0 &&
                    if (forSeries) movie.type == "TV_SERIES" else movie.type != "TV_SERIES"
        }
        val hasMore = if (forceHasMore) filtered.size >= 20 else filtered.size > 20
        val displayed = if (hasMore) filtered.take(20) else filtered
        return Pair(displayed, hasMore)
    }


    fun loadTopMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getTopMovies() // Полный список для Топ-250 (250 фильмов)
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _topMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Топ-250")
            } catch (e: Exception) {
                _topMovies.value = UiState.Error("Ошибка загрузки Топ-250: ${e.localizedMessage}")
            }
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getPopularMovies() // Полный список для Популярного (100 фильмов)
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _popularMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Популярного")
            } catch (e: Exception) {
                _popularMovies.value = UiState.Error("Ошибка загрузки Популярного: ${e.localizedMessage}")
            }
        }
    }

    fun loadPremieres() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getPremieres()
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = false)
                _premieres.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Премьер")
            } catch (e: Exception) {
                _premieres.value = UiState.Error("Ошибка загрузки Премьер: ${e.localizedMessage}")
            }
        }
    }

    fun loadActionMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getActionMovies()
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = false)
                _actionMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Боевиков США")
            } catch (e: Exception) {
                _actionMovies.value = UiState.Error("Ошибка загрузки Боевиков США: ${e.localizedMessage}")
            }
        }
    }

    fun loadDramaMovies() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getDramaMovies()
                // Устанавливаем forceHasMore = true для Драм, чтобы если их ровно 20 или больше — кнопка "Показать все" отображалась.
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = false, forceHasMore = true)
                _dramaMovies.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Драм Франции")
            } catch (e: Exception) {
                _dramaMovies.value = UiState.Error("Ошибка загрузки Драм Франции: ${e.localizedMessage}")
            }
        }
    }

    fun loadSeries() {
        viewModelScope.launch {
            try {
                val fullMovies = repository.getSeries()
                // Для категории "Сериалы" устанавливаем forceHasMore = true, чтобы, если список содержит 20 или более,
                // кнопка "Показать все" отображалась.
                val (displayed, hasMore) = processMovies(fullMovies, forSeries = true, forceHasMore = true)
                _series.value = if (displayed.isNotEmpty())
                    UiState.Success(displayed, hasMore)
                else UiState.Error("Нет данных для Сериалов")
            } catch (e: Exception) {
                _series.value = UiState.Error("Ошибка загрузки Сериалов: ${e.localizedMessage}")
            }
        }
    }
}
