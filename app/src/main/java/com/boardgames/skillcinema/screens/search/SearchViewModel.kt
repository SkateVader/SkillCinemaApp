package com.boardgames.skillcinema.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.MovieResponse
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val api: KinopoiskApi
) : ViewModel() {

    var lastSearchQuery = ""
        private set

    private val _searchResults = MutableStateFlow<MovieResponse?>(null)
    val searchResults: StateFlow<MovieResponse?> = _searchResults

    // Если фильтр не равен "Все", то список персон будет пустым
    private val _personResults = MutableStateFlow<List<PersonDetailResponse>>(emptyList())
    val personResults: StateFlow<List<PersonDetailResponse>> = _personResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun searchMovies(keyword: String, filters: SearchFilters) {
        lastSearchQuery = keyword
        viewModelScope.launch {
            if (keyword.isBlank()) {
                Log.e("SearchViewModel", "Поисковый запрос пуст!")
                return@launch
            }

            _isLoading.value = true
            _searchResults.value = null
            _personResults.value = emptyList()
            _errorMessage.value = null

            try {
                // Для фильтров "Фильмы" или "Сериалы" используем соответствующий тип,
                // для "Все" оставляем тип null, чтобы получить все типы (фильмы, сериалы)
                val typeParam = when (filters.showType) {
                    "Фильмы" -> "FILM"
                    "Сериалы" -> "TV_SHOW"
                    else -> null
                }

                val order = when (filters.sortBy) {
                    "Дата" -> "YEAR"
                    "Популярность" -> "NUM_VOTE"
                    "Рейтинг" -> "RATING"
                    else -> "NUM_VOTE"
                }

                // Поиск фильмов/сериалов
                val movieResponse = api.searchMovies(
                    keyword = keyword,
                    type = typeParam,
                    countries = filters.country?.toString(),
                    genres = filters.genre?.toString(),
                    yearFrom = filters.period.takeIf {
                        it.contains(" - ")
                    }
                        ?.split(" - ")?.getOrNull(0)?.toIntOrNull(),
                    yearTo = filters.period.takeIf {
                        it.contains(" - ")
                    }?.split(" - ")?.getOrNull(1)?.toIntOrNull(),
                    ratingFrom = filters.rating?.toInt(),
                    ratingTo = filters.rating?.let { 10 },
                    order = order,
                    page = 1
                )
                _searchResults.value = movieResponse.copy(items = movieResponse.items.orEmpty())

                // Если выбран фильтр "Все", выполняем поиск персон
                if (filters.showType == "Все") {
                    val personResponse =
                        api.searchPersons(name = keyword, page = 1)
                    Log.d("SearchViewModel", "Найдено персон: ${personResponse.total}")

                    val detailedPersons =
                        personResponse.persons.orEmpty().map { searchItem ->
                            async {
                                try {
                                    val detail =
                                        api.getPersonDetail(searchItem.kinopoiskId)
                                    PersonDetailResponse(
                                        personId = searchItem.kinopoiskId,
                                        nameRu = searchItem.nameRu,
                                        nameEn = searchItem.nameEn,
                                        profession = detail.profession ?: "Не указана",
                                        posterUrl = detail.posterUrl // URL изображения
                                    )
                                } catch (e: Exception) {
                                    PersonDetailResponse(
                                        personId = searchItem.kinopoiskId,
                                        nameRu = searchItem.nameRu,
                                        nameEn = searchItem.nameEn,
                                        profession = "Не указана",
                                        posterUrl = null
                                    )
                                }
                            }
                        }.awaitAll()

                    _personResults.value = detailedPersons
                } else {
                    _personResults.value = emptyList()
                }

            } catch (e: HttpException) {
                Log.e("SearchViewModel", "Ошибка HTTP: ${e.code()} - ${e.message()}")
                _errorMessage.value = "Во время обработки запроса произошла ошибка"
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Ошибка запроса: ${e.localizedMessage}")
                _errorMessage.value = "Во время обработки запроса произошла ошибка"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
