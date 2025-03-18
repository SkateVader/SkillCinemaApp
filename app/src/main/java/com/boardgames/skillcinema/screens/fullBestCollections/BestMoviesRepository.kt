package com.boardgames.skillcinema.screens.fullBestCollections

import android.util.Log
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import javax.inject.Inject

private const val TAG = "FullBestMoviesRepository"

class FullBestMoviesRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    suspend fun getFullBestMovies(staffId: Int): List<Movie> {
        return try {
            Log.d(TAG, "Запрос полного списка лучших фильмов для id: $staffId")
            val personDetail: PersonDetailResponse = api.getPersonDetail(staffId)
            val bestFilmIds = personDetail.films
                ?.filter { it.professionKey == "ACTOR" }
                ?.sortedByDescending { it.rating?.toFloatOrNull() ?: 0f }
                ?.mapNotNull { it.filmId }
                ?: emptyList()
            bestFilmIds.mapNotNull { filmId ->
                try {
                    api.getMovie(filmId).takeIf { it.isCompleteData() }
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка получения фильма $filmId: ${e.localizedMessage}")
                    null
                }
            }.sortedByDescending { it.ratingKinopoisk ?: 0f }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения деталей персоны: ${e.localizedMessage}")
            emptyList()
        }
    }
}
