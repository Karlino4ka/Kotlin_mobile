package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.CreateInstitutionRequestDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionOrientationDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionTypeDto
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionOrientation
import com.example.kotlin_kursach.domain.model.InstitutionType

fun InstitutionDto.toDomain(): Institution = Institution(
    id = id,
    name = name,
    type = type.toDomain(),
    orientations = orientations.orEmpty().map { it.toDomain() },
    city = city,
    address = address,
    description = description,
    phone = phone,
    website = website,
    averageRating = averageRating,
    reviewCount = reviewCount,
    photos = photos.orEmpty().map { it.toDomain() },
)

fun CreateInstitutionInput.toDto(): CreateInstitutionRequestDto = CreateInstitutionRequestDto(
    name = name,
    type = type.toDto(),
    orientations = orientations.map { it.toDto() },
    city = city,
    address = address,
    description = description,
    phone = phone,
    website = website,
)

private fun InstitutionType.toDto(): InstitutionTypeDto = when (this) {
    InstitutionType.SCHOOL -> InstitutionTypeDto.SCHOOL
    InstitutionType.COLLEGE -> InstitutionTypeDto.COLLEGE
    InstitutionType.UNIVERSITY -> InstitutionTypeDto.UNIVERSITY
}

private fun InstitutionTypeDto.toDomain(): InstitutionType = when (this) {
    InstitutionTypeDto.SCHOOL -> InstitutionType.SCHOOL
    InstitutionTypeDto.COLLEGE -> InstitutionType.COLLEGE
    InstitutionTypeDto.UNIVERSITY -> InstitutionType.UNIVERSITY
}

private fun InstitutionOrientation.toDto(): InstitutionOrientationDto = when (this) {
    InstitutionOrientation.TECHNICAL -> InstitutionOrientationDto.TECHNICAL
    InstitutionOrientation.HUMANITARIAN -> InstitutionOrientationDto.HUMANITARIAN
    InstitutionOrientation.MEDICAL -> InstitutionOrientationDto.MEDICAL
}

private fun InstitutionOrientationDto.toDomain(): InstitutionOrientation = when (this) {
    InstitutionOrientationDto.TECHNICAL -> InstitutionOrientation.TECHNICAL
    InstitutionOrientationDto.HUMANITARIAN -> InstitutionOrientation.HUMANITARIAN
    InstitutionOrientationDto.MEDICAL -> InstitutionOrientation.MEDICAL
}
