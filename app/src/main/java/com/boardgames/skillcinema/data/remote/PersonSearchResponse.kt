package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class PersonSearchResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("items") val persons: List<PersonSearchItem>
)

data class PersonSearchItem(
    @SerializedName("kinopoiskId") val kinopoiskId: Int,
    @SerializedName("webUrl") val webUrl: String,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("sex") val sex: String?,
    @SerializedName("posterUrl") val posterUrl: String?
)
