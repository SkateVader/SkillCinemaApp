package com.boardgames.skillcinema.screens.series

import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.SeriesEpisodesResponse
import javax.inject.Inject

class SeriesEpisodesRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    suspend fun getSeriesEpisodes(movieId: Int): SeriesEpisodesResponse {
        // Вызываем API по эндпоинту /api/v2.2/films/{id}/seasons (без передачи параметра season)
        return api.getSeriesEpisodes(movieId)
    }
}
