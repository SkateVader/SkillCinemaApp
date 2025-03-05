package com.boardgames.skillcinema.screens.collections

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val repository: CollectionsRepository
) : ViewModel() {

    private val favoritesKey = stringPreferencesKey("favorites")
    private val watchlistKey = stringPreferencesKey("watchlist")
    private val watchedKey = stringPreferencesKey("watched")

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

    private val _watchlist = MutableStateFlow<List<Movie>>(emptyList())
    val watchlist: StateFlow<List<Movie>> = _watchlist

    private val _watched = MutableStateFlow<List<Movie>>(emptyList())
    val watched: StateFlow<List<Movie>> = _watched

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            repository.getCollectionFlow(favoritesKey).collect { _favorites.value = it }
        }
        viewModelScope.launch {
            repository.getCollectionFlow(watchlistKey).collect { _watchlist.value = it }
        }
        viewModelScope.launch {
            repository.getCollectionFlow(watchedKey).collect { _watched.value = it }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            if (_favorites.value.any { it.id == movie.id }) {
                repository.removeMovieFromCollection(favoritesKey, movie)
                _favorites.value = _favorites.value.filter { it.id != movie.id }
            } else {
                repository.addMovieToCollection(favoritesKey, movie)
                _favorites.value = _favorites.value + movie
            }
        }
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            if (_watchlist.value.any { it.id == movie.id }) {
                repository.removeMovieFromCollection(watchlistKey, movie)
                _watchlist.value = _watchlist.value.filter { it.id != movie.id }
            } else {
                repository.addMovieToCollection(watchlistKey, movie)
                _watchlist.value = _watchlist.value + movie
            }
        }
    }

    fun toggleWatched(movie: Movie) {
        viewModelScope.launch {
            if (_watched.value.any { it.id == movie.id }) {
                repository.removeMovieFromCollection(watchedKey, movie)
                _watched.value = _watched.value.filter { it.id != movie.id }
            } else {
                repository.addMovieToCollection(watchedKey, movie)
                _watched.value = _watched.value + movie
            }
        }
    }
}
