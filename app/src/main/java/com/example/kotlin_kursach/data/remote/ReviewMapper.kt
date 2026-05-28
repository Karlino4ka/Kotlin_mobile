package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.ReviewDto
import com.example.kotlin_kursach.data.remote.dto.SubmitReviewRequestDto
import com.example.kotlin_kursach.domain.model.Review
import com.example.kotlin_kursach.domain.model.SubmitReviewInput

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    institutionId = institutionId,
    userId = userId,
    authorEmail = authorEmail,
    authorName = authorName,
    rating = rating,
    text = text,
    createdAt = createdAt,
)

fun SubmitReviewInput.toDto(): SubmitReviewRequestDto = SubmitReviewRequestDto(
    rating = rating,
    text = text,
)
