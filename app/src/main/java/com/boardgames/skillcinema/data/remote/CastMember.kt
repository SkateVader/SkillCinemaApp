package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class CastMember(
    @SerializedName("nameRu") val name: String,
    @SerializedName("posterUrl") val photoUrl: String
)
