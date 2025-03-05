package com.boardgames.skillcinema

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T, val hasMore: Boolean = false) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
