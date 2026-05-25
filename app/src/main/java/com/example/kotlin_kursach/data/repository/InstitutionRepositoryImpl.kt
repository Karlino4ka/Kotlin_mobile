package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import com.example.kotlin_kursach.data.local.toEntity
import com.example.kotlin_kursach.data.local.toDomain
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.toDomain
import com.example.kotlin_kursach.data.remote.toDto
import com.example.kotlin_kursach.domain.model.CachedData
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import retrofit2.HttpException
import javax.inject.Inject

class InstitutionRepositoryImpl @Inject constructor(
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

    override suspend fun createInstitution(input: CreateInstitutionInput): Result<Institution> =
        try {
            val created = api.createInstitution(input.toDto()).toDomain()
            institutionDao.upsert(created.toEntity())
            Result.success(created)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string().orEmpty()
            val message = parseServerErrorMessage(body) ?: "Ошибка сервера ${e.code()}"
            Result.failure(IllegalStateException(message, e))
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun parseServerErrorMessage(body: String): String? {
        if (body.isBlank()) return null
        val match = Regex(""""message"\s*:\s*"([^"]+)"""").find(body) ?: return body
        return match.groupValues[1]
    }
}
