package com.example.kotlin_kursach.data

import android.content.Context
import androidx.room.Room
import com.example.kotlin_kursach.BuildConfig
import com.example.kotlin_kursach.data.local.AppDatabase
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.repository.InstitutionRepositoryImpl
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {

    private lateinit var applicationContext: Context

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val institutionApi: InstitutionApi by lazy {
        retrofit.create(InstitutionApi::class.java)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "kotlin_kursach.db",
        ).build()
    }

    val institutionRepository: InstitutionRepository by lazy {
        InstitutionRepositoryImpl(
            api = institutionApi,
            institutionDao = database.institutionDao(),
        )
    }

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
}
