package com.boardgames.skillcinema.screens.fullCrew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.CrewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCrewViewModel @Inject constructor(
    private val fullCrewRepository: FullCrewRepository
) : ViewModel() {

    private val _crew = MutableStateFlow<UiState<List<CrewResponse>>>(UiState.Loading)
    val crew: StateFlow<UiState<List<CrewResponse>>> = _crew

    fun fetchCrew(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = fullCrewRepository.getMovieCrew(movieId)
                _crew.value = UiState.Success(response)
            } catch (e: Exception) {
                _crew.value = UiState.Error(e.message ?: "Ошибка загрузки команды фильма")
            }
        }
    }
}
