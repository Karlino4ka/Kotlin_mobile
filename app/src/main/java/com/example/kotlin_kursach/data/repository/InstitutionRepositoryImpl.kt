package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.toDomain
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.InstitutionRepository

class InstitutionRepositoryImpl(
    private val api: InstitutionApi,
) : InstitutionRepository {

    override suspend fun getInstitutions(): Result<List<Institution>> = runCatching {
        api.getInstitutions().map { it.toDomain() }
    }

    override suspend fun getInstitution(id: String): Result<Institution> = runCatching {
        api.getInstitution(id).toDomain()
    }
}
