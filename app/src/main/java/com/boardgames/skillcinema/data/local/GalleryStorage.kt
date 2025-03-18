package com.boardgames.skillcinema.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private val Context.galleryDataStore: DataStore<Preferences>
by preferencesDataStore(name = "gallery_images")

class GalleryStorage @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val GALLERY_IMAGES = stringSetPreferencesKey("gallery_images")
    }

    suspend fun saveImages(imageUrls: Set<String>) {
        context.galleryDataStore.edit { preferences ->
            preferences[PreferencesKeys.GALLERY_IMAGES] = imageUrls
        }
    }
}
