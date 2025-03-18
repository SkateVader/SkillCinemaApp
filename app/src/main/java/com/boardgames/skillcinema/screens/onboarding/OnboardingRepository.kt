package com.boardgames.skillcinema.screens.onboarding

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class OnboardingRepository @Inject constructor(
    @Named("onboarding") private val dataStore: DataStore<Preferences>
) {
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

    val isOnboardingCompleted = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }
}
