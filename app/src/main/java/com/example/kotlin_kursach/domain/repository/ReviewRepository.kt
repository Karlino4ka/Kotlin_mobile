package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.Review
import com.example.kotlin_kursach.domain.model.SubmitReviewInput

interface ReviewRepository {
    suspend fun getReviews(institutionId: String): Result<List<Review>>
    suspend fun submitReview(institutionId: String, input: SubmitReviewInput): Result<Review>
}
