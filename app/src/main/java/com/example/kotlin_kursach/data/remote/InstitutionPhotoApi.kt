package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.AddPhotoUrlRequestDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionPhotoDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface InstitutionPhotoApi {

    @Multipart
    @POST("institutions/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") institutionId: String,
        @Part file: MultipartBody.Part,
    ): InstitutionPhotoDto

    @POST("institutions/{id}/photos/url")
    suspend fun addPhotoUrl(
        @Path("id") institutionId: String,
        @Body body: AddPhotoUrlRequestDto,
    ): InstitutionPhotoDto

    @DELETE("institutions/{id}/photos/{photoId}")
    suspend fun deletePhoto(
        @Path("id") institutionId: String,
        @Path("photoId") photoId: String,
    ): Response<Unit>
}
