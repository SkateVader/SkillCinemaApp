package com.boardgames.skillcinema.screens.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchFilters(
    val showType: String = "Все",
    val country: Int? = null, // ID страны; null = Любая страна
    val genre: Int? = null,   // ID жанра; null = Любой жанр
    val period: String = "Любой год",
    val sortBy: String = "Популярность", // Сортировка по умолчанию – популярность
    val notWatched: Boolean = false,
    val rating: Float? = null // Новый параметр: рейтинг с точностью до одной десятой; null = Любой рейтинг
) : Parcelable
