package com.boardgames.skillcinema.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.boardgames.skillcinema.di.searchDataStore
import com.boardgames.skillcinema.screens.search.SearchFilters
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchFiltersPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val SHOW_TYPE = stringPreferencesKey("show_type")
        private val COUNTRY = intPreferencesKey("country")
        private val GENRE = intPreferencesKey("genre")
        private val PERIOD = stringPreferencesKey("period")
        private val SORT_BY = stringPreferencesKey("sort_by")
        private val NOT_WATCHED = booleanPreferencesKey("not_watched")
    }

    // Сохранение фильтров
    suspend fun saveFilters(filters: SearchFilters) {
        context.searchDataStore.edit { preferences ->
            preferences[SHOW_TYPE] = filters.showType
            filters.country?.let { preferences[COUNTRY] = it } ?: preferences.remove(COUNTRY)
            filters.genre?.let { preferences[GENRE] = it } ?: preferences.remove(GENRE)
            preferences[PERIOD] = filters.period
            preferences[SORT_BY] = filters.sortBy
            preferences[NOT_WATCHED] = filters.notWatched
        }
    }

    // Получение фильтров
    fun getFilters(): Flow<SearchFilters> {
        return context.searchDataStore.data.map { preferences ->
            SearchFilters(
                showType = preferences[SHOW_TYPE] ?: "Все",
                country = preferences[COUNTRY], // Вернёт null, если не установлено
                genre = preferences[GENRE],     // Вернёт null, если не установлено
                period = preferences[PERIOD] ?: "Любой год",
                sortBy = preferences[SORT_BY] ?: "Дата",
                notWatched = preferences[NOT_WATCHED] ?: false
            )
        }
    }
}

