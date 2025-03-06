package com.boardgames.skillcinema.di

import com.boardgames.skillcinema.data.remote.KinopoiskApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "https://kinopoiskapiunofficial.tech"

    // API-ключ должен быть строкой в английской раскладке без кириллических символов
    private const val API_KEY = "f4534732-cf93-49ee-8fc2-df54261b8dc1"

    //1    f22ef637-738c-4537-b89d-5a8aab6fb6dc
    //2    f952fe8f-1289-4260-b5f7-77a96e11b83b
    //3    08850bad-242e-4757-ac3d-b05fb5d88b9e
    //4    48df8539-c428-4f01-ba45-c4bc804089ca
    //5    de155a79-9c0e-4890-92a6-239219fad3a8
    //6    f4534732-cf93-49ee-8fc2-df54261b8dc1

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-API-KEY", API_KEY)
                    .build()
                chain.proceed(request)
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideKinopoiskApi(retrofit: Retrofit): KinopoiskApi {
        return retrofit.create(KinopoiskApi::class.java)
    }
}
