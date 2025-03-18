package com.boardgames.skillcinema.screens.collections

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.data.remote.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // «Просмотрено»
    private val _watched = MutableStateFlow<List<Movie>>(emptyList())
    val watched: StateFlow<List<Movie>> = _watched

    // «Вам было интересно»
    private val _interested = MutableStateFlow<List<Movie>>(emptyList())
    val interested: StateFlow<List<Movie>> = _interested

    // Пользовательские коллекции (динамические)
    private val _userCollections = MutableStateFlow<List<UserCollection>>(emptyList())
    val userCollections: StateFlow<List<UserCollection>> = _userCollections

    // Флаг для показа диалога создания коллекции
    var showCreateCollectionDialog = mutableStateOf(false)

    init {
        loadCollections()
        loadUserCollections()
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
        // Загрузка списка «Вам было интересно» из локального хранилища
        viewModelScope.launch {
            repository.getInterestedFlow().collect { _interested.value = it }
        }
    }

    private fun loadUserCollections() {
        viewModelScope.launch {
            repository.getUserCollectionsFlow().collect { _userCollections.value = it }
        }
    }

    fun toggleFavorite(movie: Movie?) {
        movie ?: return
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

    fun toggleWatchlist(movie: Movie?) {
        movie ?: return
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

    fun toggleWatched(movie: Movie?) {
        movie ?: return
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

    fun toggleUserCollection(movie: Movie?, collectionName: String, add: Boolean) {
        movie ?: return
        viewModelScope.launch {
            val updatedList = _userCollections.value.toMutableList()
            val collection = updatedList.find { it.name == collectionName }
            if (collection != null) {
                if (add) {
                    if (collection.movies.none { it.id == movie.id }) {
                        collection.movies.add(movie)
                    }
                } else {
                    collection.movies.removeIf { it.id == movie.id }
                }
            }
            _userCollections.value = updatedList
            repository.saveUserCollections(updatedList)
        }
    }

    fun createUserCollection(name: String) {
        viewModelScope.launch {
            val updatedList = _userCollections.value.toMutableList()
            if (updatedList.none { it.name == name }) {
                updatedList.add(0, UserCollection(name))
                _userCollections.value = updatedList
                repository.saveUserCollections(updatedList)
            }
        }
    }

    fun deleteUserCollection(collection: UserCollection) {
        viewModelScope.launch {
            val updatedList = _userCollections.value.toMutableList()
            updatedList.remove(collection)
            _userCollections.value = updatedList
            repository.saveUserCollections(updatedList)
        }
    }

    fun clearWatched() {
        viewModelScope.launch {
            repository.clearCollection(watchedKey)
            _watched.value = emptyList()
        }
    }

    fun addToInterested(movie: Movie?) {
        movie ?: return
        viewModelScope.launch {
            if (_interested.value.none { it.id == movie.id }) {
                val newList = _interested.value + movie
                _interested.value = newList
                repository.saveInterested(newList)
            }
        }
    }

    fun clearInterested() {
        viewModelScope.launch {
            _interested.value = emptyList()
            repository.saveInterested(emptyList())
        }
    }
}
