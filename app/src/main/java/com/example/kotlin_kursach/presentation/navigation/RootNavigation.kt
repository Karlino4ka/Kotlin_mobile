package com.example.kotlin_kursach.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.presentation.RootViewModel

@Composable
fun RootNavigation(
    rootViewModel: RootViewModel = hiltViewModel(),
) {
    val user by rootViewModel.currentUser.collectAsStateWithLifecycle()

    when (val session = user) {
        null -> AuthNavigation()
        else -> AppNavigation(
            userEmail = session.email,
            onSignOut = { rootViewModel.signOut() },
        )
    }
}
