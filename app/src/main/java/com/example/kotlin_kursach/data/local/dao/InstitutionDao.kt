package com.example.kotlin_kursach.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlin_kursach.data.local.entity.InstitutionEntity

@Dao
interface InstitutionDao {

    @Query("SELECT * FROM institutions ORDER BY name ASC")
    suspend fun getAll(): List<InstitutionEntity>

    @Query("SELECT * FROM institutions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): InstitutionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(institutions: List<InstitutionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(institution: InstitutionEntity)

    @Query("DELETE FROM institutions")
    suspend fun clearAll()
}
