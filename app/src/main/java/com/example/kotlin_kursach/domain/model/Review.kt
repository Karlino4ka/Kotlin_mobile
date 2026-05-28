package com.example.kotlin_kursach.domain.model

data class Review(
    val id: String,
    val institutionId: String,
    val userId: String,
    val authorEmail: String,
    val authorName: String?,
    val rating: Int,
    val text: String,
    val createdAt: String,
)

data class SubmitReviewInput(
    val rating: Int,
    val text: String,
)
