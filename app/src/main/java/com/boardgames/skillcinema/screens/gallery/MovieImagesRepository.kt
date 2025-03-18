package com.boardgames.skillcinema.screens.gallery

import com.boardgames.skillcinema.data.remote.GalleryResponse
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import javax.inject.Inject

class MovieImagesRepository @Inject constructor(private val api: KinopoiskApi) {
    suspend fun getMovieImages(movieId: Int, type: String): GalleryResponse {
        return api.getMovieImages(movieId, type)
    }
}
