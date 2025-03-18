package com.boardgames.skillcinema.screens.fullCollection

import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.data.remote.Movie
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.util.Calendar
import javax.inject.Inject

class FullCollectionRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    // Функция для последовательной загрузки страниц с обработкой HTTP429
    private suspend fun fetchAllPages(fetchPage: suspend (Int) -> List<Movie>): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        var page = 1
        while (true) {
            try {
                val movies = fetchPage(page)
                    .filter { it.isCompleteData() && it.id != 0 }
                if (movies.isEmpty()) break
                allMovies.addAll(movies)
                if (movies.size < 20) break
                page++
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    delay(2000L)
                    continue
                } else {
                    throw e
                }
            }
        }
        return allMovies
    }


    suspend fun getMoviesForCollection(collectionType: String): List<Movie> {
        return when (collectionType) {
            "TOP_250_BEST_FILMS" -> {
                // Загружаем все страницы для топ-250.
                fetchAllPages { page ->
                    api.getTopMovies(type = "TOP_250_BEST_FILMS", page = page)
                        .movies ?: emptyList()
                }
            }
            "TOP_100_POPULAR_FILMS" -> {
                // Загружаем все страницы для популярного.
                fetchAllPages { page ->
                    api.getPopularMovies(type = "TOP_100_POPULAR_FILMS", page = page)
                        .movies ?: emptyList()
                }
            }
            "PREMIERES" -> {
                // Премьеры, как правило, укладываются в одну страницу.
                api.getPremieres(year = 2025, month = "FEBRUARY").movies ?: emptyList()
            }
            "ACTION" -> {
                // Загружаем все страницы для фильмов с жанром "Боевик"
                // (genres = "3") снятых в США (countries = "1")
                fetchAllPages { page ->
                    api.searchMovies(
                        keyword = "",
                        countries = "1",
                        genres = "3",
                        yearFrom = 1900,
                        yearTo = Calendar.getInstance().get(Calendar.YEAR),
                        ratingFrom = 1,
                        ratingTo = 10,
                        order = "RATING",
                        page = page
                    ).movies ?: emptyList()
                }
            }
            "DRAMA" -> {
                // Загружаем все страницы для фильмов с жанром "Драма" снятых во Франции
                // (countries = "2", genres = "5")
                fetchAllPages { page ->
                    api.searchMovies(
                        keyword = "",
                        countries = "2",
                        genres = "5",
                        yearFrom = null,
                        yearTo = Calendar.getInstance().get(Calendar.YEAR),
                        ratingFrom = 1,
                        ratingTo = 10,
                        order = "RATING",
                        page = page
                    ).movies ?: emptyList()
                }
            }
            "TV_SERIES" -> {
                // Загружаем все страницы для сериалов.
                fetchAllPages { page ->
                    api.searchMovies(
                        keyword = "",
                        type = "TV_SERIES",
                        countries = null,
                        genres = null,
                        yearFrom = null,
                        yearTo = Calendar.getInstance().get(Calendar.YEAR),
                        ratingFrom = 1,
                        ratingTo = 10,
                        order = "RATING",
                        page = page
                    ).movies ?: emptyList()
                }
            }
            else -> emptyList()
        }
    }
}
