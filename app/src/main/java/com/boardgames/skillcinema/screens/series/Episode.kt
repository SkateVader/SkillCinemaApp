package com.boardgames.skillcinema.screens.series

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("episodeNumber") val episodeNumber: Int,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("releaseDate") val releaseDate: String?
) {
    val title: String
        get() = nameRu ?: nameEn ?: "Без названия"
}