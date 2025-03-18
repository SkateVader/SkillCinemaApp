package com.boardgames.skillcinema.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.boardgames.skillcinema.data.local.GalleryStorage
import com.boardgames.skillcinema.data.local.LocalMovieStorage
import com.boardgames.skillcinema.data.local.LocalMovieStorageImpl
import com.boardgames.skillcinema.data.local.LocalPersonStorage
import com.boardgames.skillcinema.data.local.LocalPersonStorageImpl
import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.screens.moviesDetails.MovieDetailsRepository
import com.boardgames.skillcinema.screens.personDetails.PersonDetailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

// Расширения для Context
val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_settings")
val Context.searchDataStore by preferencesDataStore(name = "search_filters")
val Context.personDataStore by preferencesDataStore(name = "person_details")
val Context.collectionsDataStore by preferencesDataStore(name = "movies_DataStore")

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // DataStore для онбординга
    @Provides
    @Singleton
    @Named("onboarding")
    fun provideOnboardingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.onboardingDataStore
    }

    // DataStore для поиска
    @Provides
    @Singleton
    @Named("search")
    fun provideSearchDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.searchDataStore
    }

    @Provides
    @Singleton
    fun provideGalleryStorage(@ApplicationContext context: Context): GalleryStorage {
        return GalleryStorage(context)
    }


    @Provides
    @Singleton
    fun provideMovieDetailsRepository(
        api: KinopoiskApi,
        localMovieStorage: LocalMovieStorage
    ): MovieDetailsRepository {
        return MovieDetailsRepository(api, localMovieStorage)
    }

    @Provides
    @Singleton
    fun provideLocalMovieStorage(@ApplicationContext context: Context): LocalMovieStorage {
        return LocalMovieStorageImpl(context)
    }

    @Provides
    @Singleton
    fun providePersonDetailRepository(
        api: KinopoiskApi,
        localStorage: LocalPersonStorage
    ): PersonDetailRepository {
        return PersonDetailRepository(api, localStorage)
    }

    @Provides
    @Singleton
    fun provideLocalPersonStorage(@ApplicationContext context: Context): LocalPersonStorage {
        return LocalPersonStorageImpl(context)
    }
}
