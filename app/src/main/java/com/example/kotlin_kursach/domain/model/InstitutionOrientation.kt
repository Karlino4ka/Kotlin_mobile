package com.example.kotlin_kursach.domain.model

enum class InstitutionOrientation {
    TECHNICAL,
    HUMANITARIAN,
    MEDICAL,
}

fun InstitutionOrientation.toDisplayName(): String = when (this) {
    InstitutionOrientation.TECHNICAL -> "Технический"
    InstitutionOrientation.HUMANITARIAN -> "Гуманитарный"
    InstitutionOrientation.MEDICAL -> "Медицинский"
}
