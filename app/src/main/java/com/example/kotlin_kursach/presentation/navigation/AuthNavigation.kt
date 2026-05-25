package com.example.kotlin_kursach.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_kursach.presentation.auth.LoginScreen
import com.example.kotlin_kursach.presentation.auth.SignUpScreen

object AuthRoutes {
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
}

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.LOGIN,
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(AuthRoutes.SIGN_UP) },
            )
        }
        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
    }
}
