package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class PersonDetailResponse(
    @SerializedName("personId") val personId: Int = 0,
    @SerializedName("nameRu") val nameRu: String? = null,
    @SerializedName("nameEn") val nameEn: String? = null,
    @SerializedName("sex") val sex: String? = null,
    @SerializedName("posterUrl") val posterUrl: String? = null,
    @SerializedName("growth") val growth: Int? = null,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("death") val death: String? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("birthplace") val birthplace: String? = null,
    @SerializedName("deathplace") val deathplace: String? = null,
    @SerializedName("spouses") val spouses: List<SpouseResponse>? = null,
    @SerializedName("hasAwards") val hasAwards: Int? = null,
    @SerializedName("profession") val profession: String? = null,
    @SerializedName("facts") val facts: List<String>? = null,
    @SerializedName("films") val films: List<FilmResponse>? = null
)

data class SpouseResponse(
    @SerializedName("personId") val personId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("divorced") val divorced: Boolean,
    @SerializedName("divorcedReason") val divorcedReason: String?,
    @SerializedName("sex") val sex: String,
    @SerializedName("children") val children: Int,
    @SerializedName("webUrl") val webUrl: String,
    @SerializedName("relation") val relation: String
)


data class FilmResponse(
    @SerializedName("filmId") val filmId: Int,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("general") val general: Boolean?,
    @SerializedName("description") val description: String?,
    @SerializedName("professionKey") val professionKey: String?
)
