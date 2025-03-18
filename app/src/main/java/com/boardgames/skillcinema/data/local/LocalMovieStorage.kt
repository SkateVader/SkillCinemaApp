package com.boardgames.skillcinema.data.local

import com.boardgames.skillcinema.data.remote.MovieDetailsResponse

interface LocalMovieStorage {
    suspend fun saveMovieDetails(id: Int, details: MovieDetailsResponse)
    suspend fun getMovieDetails(id: Int): MovieDetailsResponse?
}
