package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class CrewResponse(
    @SerializedName("staffId") val staffId: Int,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("professionKey") val profession: String?,
    @SerializedName("posterUrl") val posterUrl: String?
)
