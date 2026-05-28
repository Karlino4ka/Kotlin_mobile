package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.remote.ReviewApi
import com.example.kotlin_kursach.data.remote.toDomain
import com.example.kotlin_kursach.data.remote.toDto
import com.example.kotlin_kursach.domain.model.Review
import com.example.kotlin_kursach.domain.model.SubmitReviewInput
import com.example.kotlin_kursach.domain.repository.ReviewRepository
import retrofit2.HttpException
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val api: ReviewApi,
) : ReviewRepository {

    override suspend fun getReviews(institutionId: String): Result<List<Review>> =
        try {
            Result.success(api.getReviews(institutionId).map { it.toDomain() })
        } catch (e: HttpException) {
            Result.failure(mapHttpError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun submitReview(institutionId: String, input: SubmitReviewInput): Result<Review> =
        try {
            Result.success(api.submitReview(institutionId, input.toDto()).toDomain())
        } catch (e: HttpException) {
            Result.failure(mapHttpError(e))
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun mapHttpError(error: HttpException): IllegalStateException {
        val body = error.response()?.errorBody()?.string().orEmpty()
        val message = Regex(""""message"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
            ?: "Ошибка сервера ${error.code()}"
        return IllegalStateException(message, error)
    }
}
