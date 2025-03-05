package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("countries") val countries: List<Country>
)

data class Country(
    @SerializedName("country") val name: String?,
    @SerializedName("id") val id: Int?
)
