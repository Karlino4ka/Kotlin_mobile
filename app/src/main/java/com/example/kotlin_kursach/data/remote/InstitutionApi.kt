package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.InstitutionDto
import retrofit2.http.GET
import retrofit2.http.Path

interface InstitutionApi {

    @GET("institutions")
    suspend fun getInstitutions(): List<InstitutionDto>

    @GET("institutions/{id}")
    suspend fun getInstitution(@Path("id") id: String): InstitutionDto
}
