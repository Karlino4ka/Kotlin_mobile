package com.example.kotlin_kursach.di

import android.content.Context
import androidx.room.Room
import com.example.kotlin_kursach.data.local.AppDatabase
import com.example.kotlin_kursach.data.local.dao.FavoriteDao
import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kotlin_kursach.db",
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideInstitutionDao(database: AppDatabase): InstitutionDao {
        return database.institutionDao()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}
