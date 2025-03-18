package com.boardgames.skillcinema.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boardgames.skillcinema.UiState
import com.boardgames.skillcinema.data.local.GalleryStorage
import com.boardgames.skillcinema.data.remote.GalleryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieImagesViewModel @Inject constructor(
    private val repository: MovieImagesRepository,
    private val galleryStorage: GalleryStorage // Внедрение GalleryStorage
) : ViewModel() {

    private val _images = MutableStateFlow<UiState<List<GalleryItem>>>(UiState.Loading)
    val images: StateFlow<UiState<List<GalleryItem>>> = _images

    private val _availableTypes = MutableStateFlow<Map<String, Int>>(emptyMap())
    val availableTypes: StateFlow<Map<String, Int>> = _availableTypes

    fun loadImages(movieId: Int, type: String) {
        viewModelScope.launch {
            _images.value = UiState.Loading
            try {
                val response = repository.getMovieImages(movieId, type)
                _images.value = UiState.Success(response.images)

                // Сохранение URL изображений в GalleryStorage
                val imageUrls = response.images.map { it.url }.toSet()
                galleryStorage.saveImages(imageUrls)
            } catch (e: Exception) {
                _images.value = UiState.Error("Ошибка загрузки изображений: ${e.localizedMessage}")
            }
        }
    }

    fun loadAvailableTypes(movieId: Int) {
        viewModelScope.launch {
            val types =
                listOf(
                    "STILL",
                    "SHOOTING",
                    "POSTER",
                    "FAN_ART",
                    "PROMO",
                    "CONCEPT",
                    "WALLPAPER",
                    "COVER",
                    "SCREENSHOT"
                )
            val counts = mutableMapOf<String, Int>()
            types.forEach { type ->
                try {
                    val response = repository.getMovieImages(movieId, type)
                    counts[type] = response.images.size
                } catch (e: Exception) {
                    counts[type] = 0
                }
            }
            _availableTypes.value = counts
        }
    }
}
