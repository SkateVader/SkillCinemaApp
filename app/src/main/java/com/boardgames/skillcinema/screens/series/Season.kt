package com.boardgames.skillcinema.screens.series

import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("number") val seasonNumber: Int,
    @SerializedName("episodes") val episodes: List<Episode>?
)