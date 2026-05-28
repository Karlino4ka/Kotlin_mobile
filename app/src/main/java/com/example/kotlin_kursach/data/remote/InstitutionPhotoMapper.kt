package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.InstitutionPhotoDto
import com.example.kotlin_kursach.domain.model.InstitutionPhoto

fun InstitutionPhotoDto.toDomain(): InstitutionPhoto = InstitutionPhoto(
    id = id,
    url = url,
)
