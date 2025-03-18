package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName(value = "filmId", alternate = ["kinopoiskId"]) val id: Int,
    @SerializedName("nameRu") val title: String? = "Название отсутствует",
    @SerializedName("posterUrlPreview") val imageUrl: String? = null,
    @SerializedName("ratingKinopoisk") val ratingKinopoisk: Float? = 0f,
    val type: String? = null,
    @SerializedName("year") val year: Int? = 0,
    @SerializedName("genres") val genres: List<Genre>? = emptyList(),
    val role: String? = null,
    @SerializedName("description") val description: String? = null
) {
    fun isCompleteData(): Boolean {
        return !title.isNullOrBlank() && !imageUrl.isNullOrBlank()
    }

    val genre: String
        get() = genres?.firstOrNull()?.genre ?: "Неизвестный жанр"
}
