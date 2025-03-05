package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    val genres: List<Genre>
)

data class Genre(
    @SerializedName("genre") val genre: String,
    @SerializedName("id") val id: Int?
)
