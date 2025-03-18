package com.boardgames.skillcinema.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.boardgames.skillcinema.data.remote.MovieDetailsResponse
import com.boardgames.skillcinema.di.collectionsDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMovieStorageImpl @Inject constructor(
    private val context: Context
) : LocalMovieStorage {

    private val gson = Gson()

    override suspend fun saveMovieDetails(id: Int, details: MovieDetailsResponse) {
        val key = stringPreferencesKey("movie_details_$id")
        val json = gson.toJson(details)
        context.collectionsDataStore.edit { prefs ->
            prefs[key] = json
        }
    }

    override suspend fun getMovieDetails(id: Int): MovieDetailsResponse? {
        val key = stringPreferencesKey("movie_details_$id")
        val prefs = context.collectionsDataStore.data.first()
        val json = prefs[key] ?: return null
        return gson.fromJson(json, MovieDetailsResponse::class.java)
    }
}
