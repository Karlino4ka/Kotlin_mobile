package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.CachedData
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution

interface InstitutionRepository {
    suspend fun getCachedInstitutions(): List<Institution>
    suspend fun getInstitutions(): Result<CachedData<List<Institution>>>
    suspend fun getInstitution(id: String): Result<CachedData<Institution>>
    suspend fun createInstitution(input: CreateInstitutionInput): Result<Institution>
}
