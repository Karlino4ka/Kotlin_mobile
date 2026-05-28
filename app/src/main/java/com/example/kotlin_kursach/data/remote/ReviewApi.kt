package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.ReviewDto
import com.example.kotlin_kursach.data.remote.dto.SubmitReviewRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApi {

    @GET("institutions/{id}/reviews")
    suspend fun getReviews(@Path("id") institutionId: String): List<ReviewDto>

    @POST("institutions/{id}/reviews")
    suspend fun submitReview(
        @Path("id") institutionId: String,
        @Body body: SubmitReviewRequestDto,
    ): ReviewDto
}
