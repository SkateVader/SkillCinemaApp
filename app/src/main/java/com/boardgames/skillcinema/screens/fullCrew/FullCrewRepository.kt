package com.boardgames.skillcinema.screens.fullCrew

import com.boardgames.skillcinema.data.remote.CrewResponse
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import javax.inject.Inject

class FullCrewRepository @Inject constructor(
    private val kinopoiskApi: KinopoiskApi
) {
    suspend fun getMovieCrew(movieId: Int): List<CrewResponse> {
        return kinopoiskApi.getCrew(movieId)
    }
}
