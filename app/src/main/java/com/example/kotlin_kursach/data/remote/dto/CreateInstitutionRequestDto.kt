package com.example.kotlin_kursach.data.remote.dto

data class CreateInstitutionRequestDto(
    val name: String,
    val type: InstitutionTypeDto,
    val orientations: List<InstitutionOrientationDto>,
    val city: String,
    val address: String,
    val description: String,
    val phone: String? = null,
    val website: String? = null,
)
