package com.example.kotlin_kursach.data.remote.dto

data class ReviewDto(
    val id: String,
    val institutionId: String,
    val userId: String,
    val authorEmail: String,
    val authorName: String? = null,
    val rating: Int,
    val text: String,
    val createdAt: String,
)

data class SubmitReviewRequestDto(
    val rating: Int,
    val text: String,
)
