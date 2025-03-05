package com.boardgames.skillcinema.screens.details

import com.boardgames.skillcinema.data.remote.*
import javax.inject.Inject

class MovieDetailsRepository @Inject constructor(private val api: KinopoiskApi) {
    suspend fun getMovieDetails(id: Int): MovieDetailsResponse = api.getMovieDetails(id)
    suspend fun getMovieCast(id: Int): List<CastMember> = api.getMovieCast(id)
    suspend fun getMovieGallery(id: Int): GalleryResponse = api.getMovieGallery(id)
    suspend fun getSimilarMovies(id: Int): SimilarMoviesResponse = api.getSimilarMovies(id)
}
