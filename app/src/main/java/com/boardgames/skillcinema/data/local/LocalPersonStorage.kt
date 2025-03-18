package com.boardgames.skillcinema.data.local

import com.boardgames.skillcinema.data.remote.PersonDetailResponse

interface LocalPersonStorage {
    suspend fun savePersonDetail(staffId: Int, detail: PersonDetailResponse)
    suspend fun getPersonDetail(staffId: Int): PersonDetailResponse?
}
