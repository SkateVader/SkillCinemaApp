package com.boardgames.skillcinema.screens.filmography

import android.util.Log
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Movie
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import javax.inject.Inject

private const val TAG = "FilmographyRepository"

class FilmographyRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    suspend fun getFilmography(actorId: Int): List<Movie> {
        return try {
            val personDetail: PersonDetailResponse = api.getPersonDetail(actorId)
            personDetail.films?.mapNotNull { film ->
                try {
                    val completeMovie = api.getMovie(film.filmId)
                    completeMovie.copy(
                        role = film.professionKey?.uppercase(),
                        description = film.description
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка получения фильма ${film.filmId}: ${e.localizedMessage}")
                    null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения фильмографии для id: $actorId, ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getPersonDetail(actorId: Int): PersonDetailResponse {
        return try {
            api.getPersonDetail(actorId)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения деталей персоны для id: $actorId, ${e.localizedMessage}")
            PersonDetailResponse()
        }
    }
}