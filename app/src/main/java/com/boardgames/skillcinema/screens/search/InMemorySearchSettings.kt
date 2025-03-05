package com.boardgames.skillcinema.screens.search

import kotlinx.coroutines.flow.MutableStateFlow

object InMemorySearchSettings {
    // Хранит настройки поиска в рамках сессии
    val filtersFlow = MutableStateFlow(SearchFilters())
    // Счётчик-триггер для обновления поиска
    val searchTriggerFlow = MutableStateFlow(0)

    fun triggerSearch() {
        searchTriggerFlow.value++
    }
}
