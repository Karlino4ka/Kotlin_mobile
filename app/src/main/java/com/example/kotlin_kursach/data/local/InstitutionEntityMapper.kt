package com.example.kotlin_kursach.data.local

import com.example.kotlin_kursach.data.local.entity.InstitutionEntity
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionType

fun InstitutionEntity.toDomain(): Institution = Institution(
    id = id,
    name = name,
    type = InstitutionType.valueOf(type),
    city = city,
    address = address,
    description = description,
    phone = phone,
    website = website,
)

fun Institution.toEntity(): InstitutionEntity = InstitutionEntity(
    id = id,
    name = name,
    type = type.name,
    city = city,
    address = address,
    description = description,
    phone = phone,
    website = website,
)
