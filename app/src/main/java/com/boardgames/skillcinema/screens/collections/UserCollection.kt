package com.boardgames.skillcinema.screens.collections

import com.boardgames.skillcinema.data.remote.Movie

data class UserCollection(
    val name: String,
    val movies: MutableList<Movie> = mutableListOf()
)
