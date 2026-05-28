package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.CreateInstitutionRequestDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Path

interface InstitutionApi {

    @GET("institutions")
    suspend fun getInstitutions(): List<InstitutionDto>

    @GET("institutions/{id}")
    suspend fun getInstitution(@Path("id") id: String): InstitutionDto

    @POST("institutions")
    suspend fun createInstitution(@Body body: CreateInstitutionRequestDto): InstitutionDto

    @PUT("institutions/{id}")
    suspend fun updateInstitution(
        @Path("id") id: String,
        @Body body: CreateInstitutionRequestDto,
    ): InstitutionDto

    @DELETE("institutions/{id}")
    suspend fun deleteInstitution(@Path("id") id: String): Response<Unit>
}
