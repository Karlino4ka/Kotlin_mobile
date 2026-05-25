package com.example.kotlin_kursach.domain.repository

import com.example.kotlin_kursach.domain.model.UserSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserSession?>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signUp(email: String, password: String): Result<Unit>

    fun signOut()
}
