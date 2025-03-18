package com.boardgames.skillcinema.screens.fullImage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.remote.GalleryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullImageViewModel @Inject constructor(
    private val repository: FullImageRepository
) : ViewModel() {

    private val _images = MutableStateFlow<UiState<List<GalleryItem>>>(UiState.Loading)
    val images: StateFlow<UiState<List<GalleryItem>>> = _images

    fun loadImages(movieId: Int, type: String) {
        viewModelScope.launch {
            _images.value = UiState.Loading
            try {
                val response = repository.getMovieImages(movieId, type)
                _images.value = UiState.Success(response.images)
            } catch (e: Exception) {
                _images.value = UiState.Error("Ошибка загрузки изображений: ${e.localizedMessage}")
            }
        }
    }
}
