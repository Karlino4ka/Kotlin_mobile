package com.example.kotlin_kursach.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kotlin_kursach.core.AdminConfig
import com.example.kotlin_kursach.presentation.admin.InstitutionFormScreen
import com.example.kotlin_kursach.presentation.institutions.InstitutionDetailScreen
import com.example.kotlin_kursach.presentation.institutions.InstitutionListViewModel
import com.example.kotlin_kursach.presentation.main.MainScreen

const val INSTITUTION_ID_ARG = "id"

object Routes {
    const val MAIN = "main"
    const val INSTITUTION_DETAIL = "institution/{id}"
    const val ADD_INSTITUTION = "add_institution"
    const val EDIT_INSTITUTION = "edit_institution/{id}"

    fun institutionDetail(id: String) = "institution/$id"
    fun editInstitution(id: String) = "edit_institution/$id"
}

@Composable
fun AppNavigation(
    userEmail: String,
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    val isAdmin = AdminConfig.isAdmin(userEmail)

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN,
    ) {
        composable(Routes.MAIN) {
            MainScreen(
                userEmail = userEmail,
                onSignOut = onSignOut,
                onInstitutionClick = { id ->
                    navController.navigate(Routes.institutionDetail(id))
                },
                onAddInstitution = {
                    navController.navigate(Routes.ADD_INSTITUTION)
                },
            )
        }
        composable(Routes.ADD_INSTITUTION) {
            val listViewModel: InstitutionListViewModel = hiltViewModel(
                navController.getBackStackEntry(Routes.MAIN),
            )
            InstitutionFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    listViewModel.loadInstitutions()
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = Routes.EDIT_INSTITUTION,
            arguments = listOf(navArgument(INSTITUTION_ID_ARG) { type = NavType.StringType }),
        ) {
            val listViewModel: InstitutionListViewModel = hiltViewModel(
                navController.getBackStackEntry(Routes.MAIN),
            )
            InstitutionFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    listViewModel.loadInstitutions()
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = Routes.INSTITUTION_DETAIL,
            arguments = listOf(navArgument(INSTITUTION_ID_ARG) { type = NavType.StringType }),
        ) {
            val listViewModel: InstitutionListViewModel = hiltViewModel(
                navController.getBackStackEntry(Routes.MAIN),
            )
            InstitutionDetailScreen(
                onBack = { navController.popBackStack() },
                isAdmin = isAdmin,
                onEdit = { id ->
                    navController.navigate(Routes.editInstitution(id))
                },
                onDeleted = {
                    listViewModel.loadInstitutions()
                    navController.popBackStack()
                },
            )
        }
    }
}
