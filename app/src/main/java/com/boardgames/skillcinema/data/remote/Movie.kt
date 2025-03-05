package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName(value = "filmId", alternate = ["kinopoiskId"])
    val id: Int,

    @SerializedName("nameRu")
    val title: String?,

    @SerializedName("posterUrlPreview")
    val imageUrl: String?,

    @SerializedName("ratingKinopoisk")
    val ratingKinopoisk: Float?,

    val type: String? = null,

    @SerializedName("year") // Год выхода фильма
    val year: Int?,

    @SerializedName("genres") // Жанры передаются как массив, берём первый
    val genres: List<Genre>?
) {
    fun isCompleteData(): Boolean {
        return !title.isNullOrBlank() && !imageUrl.isNullOrBlank()
    }

    // Возвращает первый жанр или "Неизвестный жанр"
    val genre: String
        get() = genres?.firstOrNull()?.genre ?: "Неизвестный жанр"
}
