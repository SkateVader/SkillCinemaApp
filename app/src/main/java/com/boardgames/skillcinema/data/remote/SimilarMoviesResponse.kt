package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class SimilarMoviesResponse(
    @SerializedName("items") val movies: List<Movie>
)
