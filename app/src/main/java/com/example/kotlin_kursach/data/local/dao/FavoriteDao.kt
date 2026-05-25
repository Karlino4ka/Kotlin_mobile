package com.example.kotlin_kursach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlin_kursach.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT institutionId FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeFavoriteIds(userId: String): Flow<List<String>>

    @Query("SELECT institutionId FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    suspend fun getFavoriteIds(userId: String): List<String>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND institutionId = :institutionId)",
    )
    suspend fun isFavorite(userId: String, institutionId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND institutionId = :institutionId")
    suspend fun remove(userId: String, institutionId: String)
}
