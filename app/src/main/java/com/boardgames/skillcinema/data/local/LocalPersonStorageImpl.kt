package com.boardgames.skillcinema.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.boardgames.skillcinema.data.remote.PersonDetailResponse
import com.boardgames.skillcinema.di.personDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPersonStorageImpl @Inject constructor(
    private val context: Context
) : LocalPersonStorage {

    private val gson = Gson()

    override suspend fun savePersonDetail(staffId: Int, detail: PersonDetailResponse) {
        val key = stringPreferencesKey("person_detail_$staffId")
        val json = gson.toJson(detail)
        context.personDataStore.edit { prefs ->
            prefs[key] = json
        }
    }

    override suspend fun getPersonDetail(staffId: Int): PersonDetailResponse? {
        val key = stringPreferencesKey("person_detail_$staffId")
        val prefs = context.personDataStore.data.first()
        val json = prefs[key] ?: return null
        return gson.fromJson(json, PersonDetailResponse::class.java)
    }
}
