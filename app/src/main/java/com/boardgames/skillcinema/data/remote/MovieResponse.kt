package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("items") val items: List<Movie>? = null,
    @SerializedName("films") val films: List<Movie>? = null
) {
    // Возвращаем нужный список фильмов независимо от названия поля в JSON
    val movies: List<Movie>
        get() = items ?: films ?: emptyList()
}
