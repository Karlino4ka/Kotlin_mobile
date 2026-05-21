package com.example.kotlin_kursach.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlin_kursach.presentation.institutions.InstitutionDetailScreen
import com.example.kotlin_kursach.presentation.institutions.InstitutionListScreen

object Routes {
    const val INSTITUTIONS = "institutions"
    const val INSTITUTION_DETAIL = "institution/{id}"

    fun institutionDetail(id: String) = "institution/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.INSTITUTIONS,
    ) {
        composable(Routes.INSTITUTIONS) {
            InstitutionListScreen(
                onInstitutionClick = { id ->
                    navController.navigate(Routes.institutionDetail(id))
                },
            )
        }
        composable(
            route = Routes.INSTITUTION_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            InstitutionDetailScreen(
                institutionId = id,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
