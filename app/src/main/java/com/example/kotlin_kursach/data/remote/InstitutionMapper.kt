package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.CreateInstitutionRequestDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionTypeDto
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionType

fun InstitutionDto.toDomain(): Institution = Institution(
    id = id,
    name = name,
    type = type.toDomain(),
    city = city,
    address = address,
    description = description,
    phone = phone,
    website = website,
)

fun CreateInstitutionInput.toDto(): CreateInstitutionRequestDto = CreateInstitutionRequestDto(
    name = name,
    type = type.toDto(),
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
