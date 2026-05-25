package com.example.kotlin_kursach.domain.model

data class CreateInstitutionInput(
    val name: String,
    val type: InstitutionType,
    val city: String,
    val address: String,
    val description: String,
    val phone: String?,
    val website: String?,
)
