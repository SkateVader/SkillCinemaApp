package com.boardgames.skillcinema.screens.home

import android.util.Log
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Movie
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    // Генерирует случайный номер страницы в диапазоне от 1 до maxPage.
    private fun getRandomPage(maxPage: Int = 5): Int = (1..maxPage).random()

    suspend fun getTopMovies(): List<Movie> {
        return try {
            val response = api.getTopMovies()
            if (response.movies.isNullOrEmpty()) {
                Log.e("HomeRepository", "Топ-250 фильмов пустой список! Ответ: ${response.toString()}")
            } else {
                Log.d("HomeRepository", "Топ-250 фильмов успешно загружены: ${response.movies.size} элементов")
            }
            response.movies?.filter { it.isCompleteData() } ?: emptyList()
        } catch (e: HttpException) {
            Log.e("HomeRepository", "Ошибка HTTP при загрузке Топ-250: ${e.code()} ${e.message()}")
            emptyList()
        } catch (e: IOException) {
            Log.e("HomeRepository", "Ошибка сети при загрузке Топ-250: ${e.localizedMessage}")
            emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка при загрузке Топ-250: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getPopularMovies(): List<Movie> {
        return try {
            val response = api.getPopularMovies(page = 1)
            if (response.movies.isNullOrEmpty()) {
                Log.e("HomeRepository", "Популярные фильмы пустой список! Ответ: ${response.toString()}")
                emptyList()
            } else {
                Log.d("HomeRepository", "Популярные фильмы успешно загружены: ${response.movies.size} элементов")
                response.movies?.filter { it.isCompleteData() } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка загрузки Популярных: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getPremieres(): List<Movie> {
        return try {
            val response = api.getPremieres(year = 2025, month = "FEBRUARY") // Используем правильный формат месяца
            response.movies?.filter { it.isCompleteData() } ?: emptyList()
        } catch (e: HttpException) {
            Log.e("HomeRepository", "Ошибка HTTP при загрузке Премьер: ${e.code()} ${e.message()}")
            emptyList()
        } catch (e: IOException) {
            Log.e("HomeRepository", "Ошибка сети при загрузке Премьер: ${e.localizedMessage}")
            emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка загрузки Премьер: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getActionMovies(): List<Movie> {
        return try {
            val page = getRandomPage(5)
            // Для подборки "Боевики США": фильтрация происходит по странам и жанрам (передаём пустой keyword).
            val response = api.searchMovies(
                keyword = "",
                countries = "1", // Идентификатор США (уточните согласно документации)
                genres = "3",    // Идентификатор жанра "Боевик" (уточните согласно документации)
                yearFrom = null,
                yearTo = Calendar.getInstance().get(Calendar.YEAR),
                ratingFrom = 8,
                ratingTo = 10,
                order = "RATING",
                page = page
            )
            response.movies?.filter { it.isCompleteData() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка загрузки Боевиков США: ${e.localizedMessage}")
            emptyList()
        }
    }

    suspend fun getDramaMovies(): List<Movie> {
        return try {
            val page = getRandomPage(5)
            // Для подборки "Драмы Франции" используем ключевое слово "драма"
            // и фильтруем по странам и жанрам (countries = "2", genres = "5").
            val response = api.searchMovies(
                keyword = "",
                countries = "2", // Уточните идентификатор Франции согласно документации
                genres = "5",    // Уточните идентификатор жанра "Драма"
                yearFrom = null,
                yearTo = Calendar.getInstance().get(Calendar.YEAR),
                ratingFrom = 8,
                ratingTo = 10,
                order = "RATING",
                page = page
            )
            response.movies?.filter { it.isCompleteData() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка загрузки Драм Франции: ${e.localizedMessage}")
            emptyList()
        }
    }


    suspend fun getSeries(): List<Movie> {
        return try {
            val page = getRandomPage(5)
            // Для сериалов используем фильтр по типу "TV_SERIES".
            val response = api.searchMovies(
                keyword = "",
                type = "TV_SERIES",
                countries = null,
                genres = null,
                yearFrom = null,
                yearTo = Calendar.getInstance().get(Calendar.YEAR),
                ratingFrom = 8,
                ratingTo = 10,
                order = "RATING",
                page = page
            )
            response.movies?.filter { it.isCompleteData() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("HomeRepository", "Ошибка загрузки Сериалов: ${e.localizedMessage}")
            emptyList()
        }
    }
}
