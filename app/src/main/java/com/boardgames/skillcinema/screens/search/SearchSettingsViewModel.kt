package com.boardgames.skillcinema.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Country
import com.boardgames.skillcinema.data.remote.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSettingsViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    val filters: StateFlow<SearchFilters> = InMemorySearchSettings.filtersFlow

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        viewModelScope.launch {
            try {
                _countries.value = repository.getCountries()
            } catch (e: Exception) {
                Log.e("SearchSettingsViewModel",
                    "Ошибка загрузки стран: ${e.localizedMessage}")
                _errorMessage.value = "Во время обработки запроса произошла ошибка"
            }
            try {
                _genres.value = repository.getGenres()
            } catch (e: Exception) {
                Log.e("SearchSettingsViewModel",
                    "Ошибка загрузки жанров: ${e.localizedMessage}")
                _errorMessage.value = "Во время обработки запроса произошла ошибка"
            }
        }
    }

    fun updateFilters(updatedFilters: SearchFilters) {
        InMemorySearchSettings.filtersFlow.value = updatedFilters
    }

    fun updateSortBy(sortBy: String) {
        updateFilters(filters.value.copy(sortBy = sortBy))
    }

    fun updateGenre(newGenreId: Int?) {
        updateFilters(filters.value.copy(genre = newGenreId))
    }

    fun updateCountry(countryId: Int?) {
        updateFilters(filters.value.copy(country = countryId))
    }

    fun updateShowType(type: String) {
        updateFilters(filters.value.copy(showType = type))
    }

    fun updatePeriod(period: String) {
        updateFilters(filters.value.copy(period = period))
    }

    fun toggleNotWatched() {
        updateFilters(filters.value.copy(notWatched = !filters.value.notWatched))
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
