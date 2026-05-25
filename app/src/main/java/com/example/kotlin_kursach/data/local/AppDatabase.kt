package com.example.kotlin_kursach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import com.example.kotlin_kursach.data.local.entity.InstitutionEntity

@Database(
    entities = [InstitutionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun institutionDao(): InstitutionDao
}
