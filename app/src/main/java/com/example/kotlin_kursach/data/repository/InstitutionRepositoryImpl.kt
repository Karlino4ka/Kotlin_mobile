package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import com.example.kotlin_kursach.data.local.toEntity
import com.example.kotlin_kursach.data.local.toDomain
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.toDomain
import com.example.kotlin_kursach.domain.model.CachedData
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.InstitutionRepository

class InstitutionRepositoryImpl(
    private val api: InstitutionApi,
    private val institutionDao: InstitutionDao,
) : InstitutionRepository {

    override suspend fun getCachedInstitutions(): List<Institution> =
        institutionDao.getAll().map { it.toDomain() }

    override suspend fun getInstitutions(): Result<CachedData<List<Institution>>> {
        return try {
            val remote = api.getInstitutions().map { it.toDomain() }
            institutionDao.clearAll()
            institutionDao.upsertAll(remote.map { it.toEntity() })
            Result.success(CachedData(value = remote, fromCache = false))
        } catch (networkError: Exception) {
            val cached = institutionDao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) {
                Result.success(CachedData(value = cached, fromCache = true))
            } else {
                Result.failure(networkError)
            }
        }
    }

    override suspend fun getInstitution(id: String): Result<CachedData<Institution>> {
        return try {
            val remote = api.getInstitution(id).toDomain()
            institutionDao.upsert(remote.toEntity())
            Result.success(CachedData(value = remote, fromCache = false))
        } catch (networkError: Exception) {
            val cached = institutionDao.getById(id)?.toDomain()
            if (cached != null) {
                Result.success(CachedData(value = cached, fromCache = true))
            } else {
                Result.failure(networkError)
            }
        }
    }
}
