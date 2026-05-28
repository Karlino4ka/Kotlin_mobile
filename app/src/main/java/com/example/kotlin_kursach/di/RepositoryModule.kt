package com.example.kotlin_kursach.di

import com.example.kotlin_kursach.data.auth.FirebaseAuthRepository
import com.example.kotlin_kursach.data.repository.FavoriteRepositoryImpl
import com.example.kotlin_kursach.data.repository.InstitutionRepositoryImpl
import com.example.kotlin_kursach.data.repository.ReviewRepositoryImpl
import com.example.kotlin_kursach.domain.repository.AuthRepository
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import com.example.kotlin_kursach.domain.repository.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInstitutionRepository(
        impl: InstitutionRepositoryImpl,
    ): InstitutionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl,
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl,
    ): ReviewRepository
}
