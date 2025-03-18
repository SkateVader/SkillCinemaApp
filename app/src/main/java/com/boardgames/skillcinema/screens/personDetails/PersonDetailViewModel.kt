package com.boardgames.skillcinema.screens.personDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PersonDetailViewModel"

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val repository: PersonDetailRepository
) : ViewModel() {

    private val _personDetailState =
        MutableStateFlow<UiState<PersonDetailResponse>>(UiState.Loading)
    val personDetailState: StateFlow<UiState<PersonDetailResponse>> = _personDetailState

    private val _bestMoviesState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val bestMoviesState: StateFlow<UiState<List<Movie>>> = _bestMoviesState

    private val _totalFilmCount = MutableStateFlow(0)
    val totalFilmCount: StateFlow<Int> = _totalFilmCount

    fun loadPersonDetail(staffId: Int) {
        viewModelScope.launch {
            _personDetailState.value = UiState.Loading
            _bestMoviesState.value = UiState.Loading

            try {
                val personDetail = repository.getPersonDetail(staffId)
                _personDetailState.value = UiState.Success(personDetail)

                val uniqueFilmIds = personDetail.films?.map {
                    it.filmId }?.distinct() ?: emptyList()
                _totalFilmCount.value = uniqueFilmIds.size

                val bestMovies = repository.getBestMoviesFromPersonDetail(personDetail)
                if (bestMovies.isEmpty()) {
                    Log.w(TAG, "Список лучших фильмов пуст для персонажа $staffId")
                }
                _bestMoviesState.value = UiState.Success(bestMovies)
                Log.w(TAG, "id ======= $staffId")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка загрузки: ${e.localizedMessage}")
                _personDetailState.value = UiState.Error("Ошибка загрузки данных")
                _bestMoviesState.value = UiState.Error("Не удалось загрузить фильмы")
            }
        }
    }
}