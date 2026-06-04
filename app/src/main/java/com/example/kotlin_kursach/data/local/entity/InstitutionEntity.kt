package com.example.kotlin_kursach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "institutions")
data class InstitutionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val orientationsJson: String,
    val city: String,
    val address: String,
    val description: String,
    val phone: String?,
    val website: String?,
    val averageRating: Double?,
    val reviewCount: Int,
    val photosJson: String,
)
