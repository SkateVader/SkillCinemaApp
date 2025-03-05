package com.boardgames.skillcinema.screens.search

import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Country
import com.boardgames.skillcinema.data.remote.Genre
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    suspend fun getGenres(): List<Genre> {
        val response = api.getGenres()
        return response.genres
            .filter { it.genre.isNotBlank() }
            .sortedBy { it.genre }
    }

    suspend fun getCountries(): List<Country> {
        val response = api.getCountries()
        return response.countries
            .filter { !it.name.isNullOrBlank() }
            .sortedBy { it.name }
    }
}
