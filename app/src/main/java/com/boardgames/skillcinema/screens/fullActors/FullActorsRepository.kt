package com.boardgames.skillcinema.screens.fullActors

import com.boardgames.skillcinema.data.remote.CastResponse
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import javax.inject.Inject

class FullActorsRepository @Inject constructor(
    private val kinopoiskApi: KinopoiskApi
) {
    suspend fun getMovieCast(movieId: Int): List<CastResponse> {
        return kinopoiskApi.getMovieCast(movieId)
    }
}
