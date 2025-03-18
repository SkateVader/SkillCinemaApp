package com.boardgames.skillcinema.screens.moviesDetails

import com.boardgames.skillcinema.data.remote.*
import com.boardgames.skillcinema.data.local.LocalMovieStorage
import javax.inject.Inject

class MovieDetailsRepository @Inject constructor(
    private val api: KinopoiskApi,
    private val localStorage: LocalMovieStorage
) {

    suspend fun getMovieDetails(id: Int): MovieDetailsResponse {
        return localStorage.getMovieDetails(id) ?: run {
            val details = api.getMovieDetails(id)
            localStorage.saveMovieDetails(id, details)
            details
        }
    }

    suspend fun getMovieCast(id: Int): List<CastResponse> = api.getMovieCast(id)
    suspend fun getMovieCrew(id: Int): List<CrewResponse> = api.getCrew(id)
    suspend fun getMovieGallery(id: Int): GalleryResponse = api.getMovieGallery(id)
    suspend fun getSimilarMovies(id: Int): SimilarMoviesResponse = api.getSimilarMovies(id)
    suspend fun getSeriesEpisodes(movieId: Int): SeriesEpisodesResponse =
        api.getSeriesEpisodes(movieId)
}