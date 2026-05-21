package com.example.kotlin_kursach.data.remote.dto

data class InstitutionDto(
    val id: String,
    val name: String,
    val type: InstitutionTypeDto,
    val city: String,
    val address: String,
    val description: String,
    val phone: String? = null,
    val website: String? = null,
)

enum class InstitutionTypeDto {
    SCHOOL,
    COLLEGE,
    UNIVERSITY,
}
