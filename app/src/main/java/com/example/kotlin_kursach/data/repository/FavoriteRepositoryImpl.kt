package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.core.AdminConfig
import com.example.kotlin_kursach.data.local.dao.FavoriteDao
import com.example.kotlin_kursach.data.local.entity.FavoriteEntity
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.AuthRepository
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository,
) : FavoriteRepository {

    override fun observeFavoriteIds(): Flow<Set<String>> =
        authRepository.currentUser.flatMapLatest { user ->
            if (user == null || AdminConfig.isAdmin(user.email)) {
                flowOf(emptySet())
            } else {
                favoriteDao.observeFavoriteIds(user.uid).map { it.toSet() }
            }
        }

    override suspend fun toggleFavorite(institutionId: String) {
        if (isCurrentUserAdmin()) return
        val userId = currentUserId() ?: return
        if (favoriteDao.isFavorite(userId, institutionId)) {
            favoriteDao.remove(userId, institutionId)
        } else {
            favoriteDao.add(
                FavoriteEntity(
                    userId = userId,
                    institutionId = institutionId,
                ),
            )
        }
    }

    override suspend fun isFavorite(institutionId: String): Boolean {
        if (isCurrentUserAdmin()) return false
        val userId = currentUserId() ?: return false
        return favoriteDao.isFavorite(userId, institutionId)
    }

    override suspend fun getFavoriteInstitutions(allInstitutions: List<Institution>): List<Institution> {
        if (isCurrentUserAdmin()) return emptyList()
        val userId = currentUserId() ?: return emptyList()
        val ids = favoriteDao.getFavoriteIds(userId).toSet()
        return allInstitutions.filter { it.id in ids }
    }

    private fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    private fun isCurrentUserAdmin(): Boolean =
        AdminConfig.isAdmin(firebaseAuth.currentUser?.email)
}
