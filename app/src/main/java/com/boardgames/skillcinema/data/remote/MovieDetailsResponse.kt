package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("nameRu") val title: String,
    @SerializedName("nameOriginal") val originalTitle: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("year") val year: Int,
    @SerializedName("filmLength") val filmLength: Int?, // длительность фильма в минутах
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("countries") val countries: List<Country>,
    @SerializedName("ratingKinopoisk") val ratingKinopoisk: Float?,
    @SerializedName("imdbId") val imdbId: String?,
    @SerializedName("type") val type: String,
    @SerializedName("seasonsCount") val seasonsCount: Int?,
    @SerializedName("episodesCount") val episodesCount: Int?,
    @SerializedName("ratingAgeLimits") val ratingAgeLimits: String?
) {
    // Вычисляемое свойство для отображения страны (берется первая из списка)
    val country: String?
        get() = if (countries.isNotEmpty()) countries[0].name else null

}
