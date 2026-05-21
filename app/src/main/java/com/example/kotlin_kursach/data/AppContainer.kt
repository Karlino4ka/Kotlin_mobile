package com.example.kotlin_kursach.data

import com.example.kotlin_kursach.BuildConfig
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.repository.InstitutionRepositoryImpl
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val institutionApi: InstitutionApi = retrofit.create(InstitutionApi::class.java)

    val institutionRepository: InstitutionRepository =
        InstitutionRepositoryImpl(institutionApi)
}
