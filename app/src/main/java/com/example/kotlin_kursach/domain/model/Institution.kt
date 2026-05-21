package com.example.kotlin_kursach.domain.model

data class Institution(
    val id: String,
    val name: String,
    val type: InstitutionType,
    val city: String,
    val address: String,
    val description: String,
    val phone: String?,
    val website: String?,
)
