package com.example.kotlin_kursach.data.auth

import com.example.kotlin_kursach.domain.model.UserSession
import com.example.kotlin_kursach.domain.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override val currentUser: StateFlow<UserSession?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUserSession())
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.toUserSession())
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.stateIn(scope, started = kotlinx.coroutines.flow.SharingStarted.Eagerly, initialValue = firebaseAuth.currentUser?.toUserSession())

    override suspend fun signIn(email: String, password: String): Result<Unit> = authCall {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> = authCall {
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun com.google.firebase.auth.FirebaseUser.toUserSession(): UserSession = UserSession(
        uid = uid,
        email = email.orEmpty(),
    )

}

private suspend fun authCall(block: suspend () -> Unit): Result<Unit> = try {
    block()
    Result.success(Unit)
} catch (e: Exception) {
    Result.failure(mapFirebaseException(e))
}

internal fun mapFirebaseException(throwable: Throwable): Throwable {
    if (throwable !is FirebaseException) return throwable
    val message = when (throwable) {
        is FirebaseAuthInvalidUserException -> "Пользователь не найден"
        is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
        is FirebaseAuthUserCollisionException -> "Этот email уже зарегистрирован"
        is FirebaseAuthWeakPasswordException -> "Пароль должен быть не короче 6 символов"
        else -> throwable.localizedMessage ?: "Ошибка авторизации"
    }
    return IllegalStateException(message, throwable)
}
