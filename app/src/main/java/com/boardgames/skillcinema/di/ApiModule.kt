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
    private const val API_KEY = "d05455cb-e0d9-4f81-a562-cbb11e4195d6"

    //1    f22ef637-738c-4537-b89d-5a8aab6fb6dc
    //2    f952fe8f-1289-4260-b5f7-77a96e11b83b
    //3    08850bad-242e-4757-ac3d-b05fb5d88b9e
    //4    48df8539-c428-4f01-ba45-c4bc804089ca
    //5    de155a79-9c0e-4890-92a6-239219fad3a8
    //6    f4534732-cf93-49ee-8fc2-df54261b8dc1
    //7    6f23a4d4-cb35-40a3-9e6e-18478e19f7ef
    //8    1636c469-e0c9-476a-a41a-fbae5c947737
    //9    2e093251-89b1-4168-9b2b-f86eaa55dad4
    //10   c994246b-9a95-4950-ae4e-6f3f4762b16e
    //11   9e4b3b97-770d-4096-b5a4-6c5f68d8aa08
    //12   d68ee0a8-8daa-4621-9703-d90f6e115e93
    //13   d05455cb-e0d9-4f81-a562-cbb11e4195d6
    //14   3fce2e0a-1805-4092-a14f-34703b162e2f
    //15   d853ec9f-5477-427e-a7a3-d144a5a9405f

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
