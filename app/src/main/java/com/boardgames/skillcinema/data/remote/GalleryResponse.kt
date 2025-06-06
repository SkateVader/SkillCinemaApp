package com.boardgames.skillcinema.data.remote

import com.google.gson.annotations.SerializedName

data class GalleryResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("items") val images: List<GalleryItem>
)

data class GalleryItem(
    @SerializedName("imageUrl") val url: String
)
