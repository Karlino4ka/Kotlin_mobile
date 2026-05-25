package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.Institution
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun toggleFavorite(institutionId: String)
    suspend fun isFavorite(institutionId: String): Boolean
    suspend fun getFavoriteInstitutions(allInstitutions: List<Institution>): List<Institution>
}
