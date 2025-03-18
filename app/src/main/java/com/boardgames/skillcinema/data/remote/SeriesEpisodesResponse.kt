package com.boardgames.skillcinema.data.remote

import com.boardgames.skillcinema.screens.series.Season
import com.google.gson.annotations.SerializedName

data class SeriesEpisodesResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("items") val seasons: List<Season>?
)

