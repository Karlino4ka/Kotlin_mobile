package com.example.kotlin_kursach.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "institutionId"],
)
data class FavoriteEntity(
    val userId: String,
    val institutionId: String,
    val addedAt: Long = System.currentTimeMillis(),
)
