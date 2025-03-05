package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("nameRu") val title: String,
    @SerializedName("nameOriginal") val originalTitle: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("year") val year: Int,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("ratingKinopoisk") val ratingKinopoisk: Float?,
    @SerializedName("imdbId") val imdbId: String,
    @SerializedName("seasonsCount") val seasonsCount: Int?
)
