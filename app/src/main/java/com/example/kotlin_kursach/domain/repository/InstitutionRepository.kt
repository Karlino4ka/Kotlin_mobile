package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.Institution

interface InstitutionRepository {
    suspend fun getInstitutions(): Result<List<Institution>>
    suspend fun getInstitution(id: String): Result<Institution>
}
