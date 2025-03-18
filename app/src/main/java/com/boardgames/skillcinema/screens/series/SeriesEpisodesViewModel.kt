package com.boardgames.skillcinema.screens.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesEpisodesViewModel @Inject constructor(
    private val repository: SeriesEpisodesRepository
) : ViewModel() {
    private val _episodes = MutableStateFlow<UiState<List<Episode>>>(UiState.Loading)
    val episodes: StateFlow<UiState<List<Episode>>> = _episodes

    var episodesCount: Int = 0
        private set

    fun loadEpisodes(movieId: Int, season: Int) {
        viewModelScope.launch {
            _episodes.value = UiState.Loading
            try {
                // Получаем данные о всех сезонах
                val response = repository.getSeriesEpisodes(movieId)
                // Находим сезон с нужным номером
                val selectedSeason = response.seasons?.find { it.seasonNumber == season }
                episodesCount = selectedSeason?.episodes?.size ?: 0
                _episodes.value = UiState.Success(selectedSeason?.episodes ?: emptyList())
            } catch (e: Exception) {
                _episodes.value = UiState.Error("Ошибка загрузки серий: ${e.localizedMessage}")
            }
        }
    }
}
