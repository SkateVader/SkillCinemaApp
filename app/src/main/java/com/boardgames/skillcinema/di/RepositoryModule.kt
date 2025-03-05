package com.boardgames.skillcinema.di

import com.boardgames.skillcinema.data.remote.KinopoiskApi
import com.boardgames.skillcinema.screens.details.MovieDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieDetailsRepository(api: KinopoiskApi): MovieDetailsRepository {
        return MovieDetailsRepository(api)
    }
}
