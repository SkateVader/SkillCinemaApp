package com.boardgames.skillcinema.screens.collections

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.boardgames.skillcinema.data.remote.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "collections")

class CollectionsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    fun getCollectionFlow(key: Preferences.Key<String>): Flow<List<Movie>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[key] ?: "[]"
            gson.fromJson(json, object : TypeToken<List<Movie>>() {}.type) ?: emptyList()
        }
    }

    suspend fun addMovieToCollection(key: Preferences.Key<String>, movie: Movie) {
        context.dataStore.edit { prefs ->
            val movies = getCollection(key).toMutableList()
            if (movies.none { it.id == movie.id }) {
                movies.add(movie)
                prefs[key] = gson.toJson(movies)
            }
        }
    }

    suspend fun removeMovieFromCollection(key: Preferences.Key<String>, movie: Movie) {
        context.dataStore.edit { prefs ->
            val movies = getCollection(key).toMutableList()
            movies.removeIf { it.id == movie.id }
            prefs[key] = gson.toJson(movies)
        }
    }

    fun getCollection(key: Preferences.Key<String>): List<Movie> {
        return runBlocking { getCollectionFlow(key).first() }
    }
}
