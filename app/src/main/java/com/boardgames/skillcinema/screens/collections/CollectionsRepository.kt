package com.boardgames.skillcinema.screens.collections

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.boardgames.skillcinema.data.remote.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.collectionsDataStore by preferencesDataStore(name = "collectionsDataStore")

class CollectionsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    fun getCollectionFlow(key: Preferences.Key<String>): Flow<List<Movie>> {
        return context.collectionsDataStore.data.map { prefs ->
            val json = prefs[key] ?: "[]"
            gson.fromJson(json, object : TypeToken<List<Movie>>() {}.type) ?: emptyList()
        }
    }

    suspend fun addMovieToCollection(key: Preferences.Key<String>, movie: Movie) {
        context.collectionsDataStore.edit { prefs ->
            val movies = getCollection(key).toMutableList()
            if (movies.none { it.id == movie.id }) {
                movies.add(movie)
                prefs[key] = gson.toJson(movies)
            }
        }
    }

    suspend fun removeMovieFromCollection(key: Preferences.Key<String>, movie: Movie) {
        context.collectionsDataStore.edit { prefs ->
            val movies = getCollection(key).toMutableList()
            movies.removeIf { it.id == movie.id }
            prefs[key] = gson.toJson(movies)
        }
    }

    fun getCollection(key: Preferences.Key<String>): List<Movie> {
        return runBlocking { getCollectionFlow(key).first() }
    }

    suspend fun clearCollection(key: Preferences.Key<String>) {
        context.collectionsDataStore.edit { prefs ->
            prefs[key] = "[]"
        }
    }

    // Реализация для пользовательских коллекций
    private val USER_COLLECTIONS_KEY = stringPreferencesKey("user_collections")

    fun getUserCollectionsFlow(): Flow<List<UserCollection>> {
        return context.collectionsDataStore.data.map { prefs ->
            val json = prefs[USER_COLLECTIONS_KEY] ?: "[]"
            gson.fromJson(json, object : TypeToken<List<UserCollection>>() {}.type)
                ?: emptyList()
        }
    }

    suspend fun saveUserCollections(collections: List<UserCollection>) {
        context.collectionsDataStore.edit { prefs ->
            prefs[USER_COLLECTIONS_KEY] = gson.toJson(collections)
        }
    }

    // Новый ключ и методы для списка «Вам было интересно»
    private val INTERESTED_KEY = stringPreferencesKey("interested")

    fun getInterestedFlow(): Flow<List<Movie>> {
        return context.collectionsDataStore.data.map { prefs ->
            val json = prefs[INTERESTED_KEY] ?: "[]"
            gson.fromJson(json, object : TypeToken<List<Movie>>() {}.type) ?: emptyList()
        }
    }

    suspend fun saveInterested(movies: List<Movie>) {
        context.collectionsDataStore.edit { prefs ->
            prefs[INTERESTED_KEY] = gson.toJson(movies)
        }
    }
}
