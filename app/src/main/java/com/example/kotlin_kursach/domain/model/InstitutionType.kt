package com.example.kotlin_kursach.domain.model

enum class InstitutionType {
    SCHOOL,
    COLLEGE,
    UNIVERSITY,
}

fun InstitutionType.toDisplayName(): String = when (this) {
    InstitutionType.SCHOOL -> "Школа"
    InstitutionType.COLLEGE -> "Колледж"
    InstitutionType.UNIVERSITY -> "Вуз"
}
