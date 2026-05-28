package com.example.kotlin_kursach.data.remote.dto

data class InstitutionPhotoDto(
    val id: String,
    val url: String,
)

data class AddPhotoUrlRequestDto(
    val url: String,
)
