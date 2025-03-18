package com.boardgames.skillcinema.screens.filmography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfessionItem(val profession: String, val count: Int)

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    private val repository: FilmographyRepository
) : ViewModel() {

    private val _filmographyState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val filmographyState: StateFlow<UiState<List<Movie>>> = _filmographyState

    private val _filteredFilms = MutableStateFlow<List<Movie>>(emptyList())
    val filteredFilms: StateFlow<List<Movie>> = _filteredFilms

    private val _availableProfessions = MutableStateFlow<List<ProfessionItem>>(emptyList())
    val availableProfessions: StateFlow<List<ProfessionItem>> = _availableProfessions

    private val _selectedProfession = MutableStateFlow<String?>(null)
    val selectedProfession: StateFlow<String?> = _selectedProfession

    private val _personSex = MutableStateFlow("MALE")
    val personSex: StateFlow<String> = _personSex

    private var fullFilmography: List<Movie> = emptyList()

    fun loadFilmography(actorId: Int) {
        viewModelScope.launch {
            _filmographyState.value = UiState.Loading
            try {
                val personDetail = repository.getPersonDetail(actorId)
                _personSex.value = personDetail.sex ?: "MALE"
                val films = repository.getFilmography(actorId)
                fullFilmography = films
                _filmographyState.value = UiState.Success(films)

                // Разделение фильмов по профессиям
                val professionsMap =
                    mutableMapOf<String, MutableList<Movie>>()
                films.forEach { film ->
                    val role = film.role ?: "UNKNOWN"
                    if (role == "ACTOR"
                        && film.description?.contains("озвучка", ignoreCase = true) == true
                    ) {
                        professionsMap.getOrPut("VOICE") { mutableListOf() }.add(film)
                    } else {
                        professionsMap.getOrPut(role) { mutableListOf() }.add(film)
                    }
                }

                val professionsWithCount = professionsMap.map { (profession, movies) ->
                    ProfessionItem(profession, movies.size)
                }.sortedByDescending { it.count }

                _availableProfessions.value = professionsWithCount

                if (professionsWithCount.isNotEmpty()) {
                    _selectedProfession.value = professionsWithCount.first().profession
                }
                applyFilter()
            } catch (e: Exception) {
                _filmographyState.value = UiState.Error("Ошибка загрузки фильмографии")
            }
        }
    }

    fun onFilterSelected(profession: String) {
        _selectedProfession.value = profession
        applyFilter()
    }

    private fun applyFilter() {
        val selected = _selectedProfession.value
        if (selected != null) {
            val filtered = fullFilmography.filter { film ->
                when (selected) {
                    "ACTOR" -> film.role == "ACTOR" && film.description?.contains(
                        "озвучка", ignoreCase = true
                    ) != true

                    "VOICE" -> film.role == "ACTOR" && film.description?.contains(
                        "озвучка", ignoreCase = true
                    ) == true

                    else -> film.role == selected
                }
            }
            _filteredFilms.value = filtered.sortedByDescending { it.ratingKinopoisk ?: 0f }
        } else {
            _filteredFilms.value = emptyList()
        }
    }
}