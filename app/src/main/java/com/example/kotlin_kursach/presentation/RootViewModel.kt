package com.example.kotlin_kursach.presentation

import androidx.lifecycle.ViewModel
import com.example.kotlin_kursach.domain.model.UserSession
import com.example.kotlin_kursach.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val currentUser: StateFlow<UserSession?> = authRepository.currentUser

    fun signOut() {
        authRepository.signOut()
    }
}
