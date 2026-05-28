package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.CachedData
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionPhoto

interface InstitutionRepository {
    suspend fun getCachedInstitutions(): List<Institution>
    suspend fun getInstitutions(): Result<CachedData<List<Institution>>>
    suspend fun getInstitution(id: String): Result<CachedData<Institution>>
    suspend fun createInstitution(input: CreateInstitutionInput): Result<Institution>
    suspend fun updateInstitution(id: String, input: CreateInstitutionInput): Result<Institution>
    suspend fun deleteInstitution(id: String): Result<Unit>
    suspend fun uploadPhoto(
        institutionId: String,
        fileName: String,
        bytes: ByteArray,
        mimeType: String,
    ): Result<InstitutionPhoto>
    suspend fun addPhotoUrl(institutionId: String, url: String): Result<InstitutionPhoto>
    suspend fun deletePhoto(institutionId: String, photoId: String): Result<Unit>
}
