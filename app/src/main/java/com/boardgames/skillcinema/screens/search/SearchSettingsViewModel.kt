package com.boardgames.skillcinema.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Country
import com.boardgames.skillcinema.data.remote.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchSettingsViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    // Используем общий поток настроек из InMemorySearchSettings
    val filters: StateFlow<SearchFilters> = InMemorySearchSettings.filtersFlow

    // Потоки для стран и жанров (подгружаются из репозитория)
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    init {
        viewModelScope.launch {
            _countries.value = repository.getCountries()
            _genres.value = repository.getGenres()
        }
    }

    // Обновление фильтров: изменения сохраняются в общем StateFlow
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
}
