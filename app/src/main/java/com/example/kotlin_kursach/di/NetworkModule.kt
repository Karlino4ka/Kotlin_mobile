package com.example.kotlin_kursach.di

import com.example.kotlin_kursach.BuildConfig
import com.example.kotlin_kursach.data.remote.AdminAuthInterceptor
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.InstitutionPhotoApi
import com.example.kotlin_kursach.data.remote.ReviewApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(AdminAuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInstitutionApi(retrofit: Retrofit): InstitutionApi {
        return retrofit.create(InstitutionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewApi(retrofit: Retrofit): ReviewApi {
        return retrofit.create(ReviewApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInstitutionPhotoApi(retrofit: Retrofit): InstitutionPhotoApi {
        return retrofit.create(InstitutionPhotoApi::class.java)
    }
}
