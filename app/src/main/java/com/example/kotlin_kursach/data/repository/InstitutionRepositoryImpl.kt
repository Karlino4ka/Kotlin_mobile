package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import com.example.kotlin_kursach.data.local.toEntity
import com.example.kotlin_kursach.data.local.toDomain
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.InstitutionPhotoApi
import com.example.kotlin_kursach.data.remote.dto.AddPhotoUrlRequestDto
import com.example.kotlin_kursach.data.remote.toDomain
import com.example.kotlin_kursach.data.remote.toDto
import com.example.kotlin_kursach.domain.model.CachedData
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionPhoto
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

class InstitutionRepositoryImpl @Inject constructor(
    private val api: InstitutionApi,
    private val photoApi: InstitutionPhotoApi,
    private val institutionDao: InstitutionDao,
) : InstitutionRepository {

    override suspend fun getCachedInstitutions(): List<Institution> =
        institutionDao.getAll().map { it.toDomain() }

    override suspend fun getInstitutions(): Result<CachedData<List<Institution>>> {
        return try {
            val remote = api.getInstitutions().orEmpty().map { it.toDomain() }
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
        runMutation {
            val created = api.createInstitution(input.toDto()).toDomain()
            institutionDao.upsert(created.toEntity())
            created
        }

    override suspend fun updateInstitution(id: String, input: CreateInstitutionInput): Result<Institution> =
        runMutation {
            val updated = api.updateInstitution(id, input.toDto()).toDomain()
            institutionDao.upsert(updated.toEntity())
            updated
        }

    override suspend fun deleteInstitution(id: String): Result<Unit> =
        runMutation {
            val response = api.deleteInstitution(id)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            institutionDao.deleteById(id)
        }

    override suspend fun uploadPhoto(
        institutionId: String,
        fileName: String,
        bytes: ByteArray,
        mimeType: String,
    ): Result<InstitutionPhoto> = runMutation {
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        photoApi.uploadPhoto(institutionId, part).toDomain()
    }

    override suspend fun addPhotoUrl(institutionId: String, url: String): Result<InstitutionPhoto> =
        runMutation {
            photoApi.addPhotoUrl(institutionId, AddPhotoUrlRequestDto(url.trim())).toDomain()
        }

    override suspend fun deletePhoto(institutionId: String, photoId: String): Result<Unit> =
        runMutation {
            val response = photoApi.deletePhoto(institutionId, photoId)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }

    private suspend fun <T> runMutation(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
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
