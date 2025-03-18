package com.boardgames.skillcinema.screens.personDetails

import android.util.Log
import com.boardgames.skillcinema.data.local.LocalPersonStorage
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import javax.inject.Inject

private const val TAG = "PersonDetailRepository"

class PersonDetailRepository @Inject constructor(
    private val api: KinopoiskApi,
    private val localStorage: LocalPersonStorage
) {
    suspend fun getPersonDetail(staffId: Int): PersonDetailResponse {
        val detail = try {
            Log.d(TAG, "Выполнение запроса к API для id: $staffId")
            api.getPersonDetail(staffId)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка запроса к API для id: $staffId, исключение: " +
                    "${e.localizedMessage}")
            PersonDetailResponse()
        }
        localStorage.savePersonDetail(staffId, detail)
        return detail
    }

    suspend fun getBestMoviesFromPersonDetail(personDetail: PersonDetailResponse): List<Movie> {
        val films = personDetail.films ?: return emptyList()
        val sortedFilms = films.sortedByDescending {
            it.rating?.toFloatOrNull() ?: 0f }
        val uniqueFilmIds = mutableSetOf<Int>()
        val topFilms = sortedFilms.filter {
            if (uniqueFilmIds.contains(it.filmId)) false
            else {
                uniqueFilmIds.add(it.filmId)
                true
            }
        }.take(20)
        return topFilms.mapNotNull { film ->
            try {
                api.getMovie(film.filmId).takeIf { it.isCompleteData() }
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка получения фильма ${film.filmId}: ${e.localizedMessage}")
                null
            }
        }.sortedByDescending { it.ratingKinopoisk ?: 0f }
    }
}