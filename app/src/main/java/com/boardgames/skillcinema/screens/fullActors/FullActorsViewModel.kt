package com.boardgames.skillcinema.screens.fullActors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.CastResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullActorsViewModel @Inject constructor(
    private val fullActorsRepository: FullActorsRepository
) : ViewModel() {

    private val _actors = MutableStateFlow<UiState<List<CastResponse>>>(UiState.Loading)
    val actors: StateFlow<UiState<List<CastResponse>>> = _actors

    fun fetchActors(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = fullActorsRepository.getMovieCast(movieId)
                _actors.value = UiState.Success(response)
            } catch (e: Exception) {
                Log.e("FullActorsViewModel", "Ошибка загрузки актёров", e)
                _actors.value =
                    UiState.Error(e.message ?: "Во время обработки запроса произошла ошибка")
            }
        }
    }
}
