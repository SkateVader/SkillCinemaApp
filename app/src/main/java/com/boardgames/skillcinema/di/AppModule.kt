package com.boardgames.skillcinema.di

import android.content.Context
import com.boardgames.skillcinema.data.local.SearchFiltersPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSearchFiltersPreferences(@ApplicationContext context: Context): SearchFiltersPreferences {
        return SearchFiltersPreferences(context)
    }
}
