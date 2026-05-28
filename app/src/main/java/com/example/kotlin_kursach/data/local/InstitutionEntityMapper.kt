package com.example.kotlin_kursach.data.local



import com.example.kotlin_kursach.data.local.entity.InstitutionEntity

import com.example.kotlin_kursach.domain.model.Institution

import com.example.kotlin_kursach.domain.model.InstitutionPhoto

import com.example.kotlin_kursach.domain.model.InstitutionType

import com.google.gson.Gson

import com.google.gson.reflect.TypeToken



private val gson = Gson()

private val photoListType = object : TypeToken<List<InstitutionPhoto>>() {}.type



fun InstitutionEntity.toDomain(): Institution = Institution(

    id = id,

    name = name,

    type = InstitutionType.valueOf(type),

    city = city,

    address = address,

    description = description,

    phone = phone,

    website = website,

    averageRating = averageRating,

    reviewCount = reviewCount,

    photos = parsePhotos(photosJson),

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

    averageRating = averageRating,

    reviewCount = reviewCount,

    photosJson = gson.toJson(photos),

)



private fun parsePhotos(json: String): List<InstitutionPhoto> {
    if (json.isBlank()) return emptyList()
    return runCatching { gson.fromJson<List<InstitutionPhoto>>(json, photoListType) }
        .getOrNull()
        .orEmpty()
}

