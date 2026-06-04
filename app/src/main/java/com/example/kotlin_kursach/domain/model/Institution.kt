package com.example.kotlin_kursach.domain.model

data class Institution(
    val id: String,
    val name: String,
    val type: InstitutionType,
    val orientations: List<InstitutionOrientation> = emptyList(),
    val city: String,
    val address: String,
    val description: String,
    val phone: String?,
    val website: String?,
    val averageRating: Double? = null,
    val reviewCount: Int = 0,
    val photos: List<InstitutionPhoto> = emptyList(),
)
