package com.example.kotlin_kursach.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.core.AdminConfig
import com.example.kotlin_kursach.presentation.catalog.CatalogScreen
import com.example.kotlin_kursach.presentation.favorites.FavoritesScreen
import com.example.kotlin_kursach.presentation.institutions.InstitutionListViewModel
import com.example.kotlin_kursach.presentation.profile.ProfileScreen

private enum class MainTab(val label: String) {
    Catalog("Каталог"),
    Favorites("Избранное"),
    Profile("Профиль"),
}

@Composable
fun MainScreen(
    userEmail: String,
    onSignOut: () -> Unit,
    onInstitutionClick: (String) -> Unit,
    onAddInstitution: () -> Unit,
    catalogViewModel: InstitutionListViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Catalog) }
    val catalogState by catalogViewModel.screenState.collectAsStateWithLifecycle()
    val isAdmin = AdminConfig.isAdmin(userEmail)
    val visibleTabs = remember(isAdmin) {
        if (isAdmin) listOf(MainTab.Catalog, MainTab.Profile) else MainTab.entries
    }

    val activeTab = if (isAdmin && selectedTab == MainTab.Favorites) {
        MainTab.Catalog
    } else {
        selectedTab
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                visibleTabs.forEach { tab ->
                    val selected = activeTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    MainTab.Catalog -> if (selected) Icons.Filled.School else Icons.Outlined.School
                                    MainTab.Favorites -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                                    MainTab.Profile -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (activeTab) {
            MainTab.Catalog -> CatalogScreen(
                onInstitutionClick = onInstitutionClick,
                isAdmin = isAdmin,
                onAddInstitution = onAddInstitution,
                modifier = Modifier.padding(innerPadding),
            )
            MainTab.Favorites -> FavoritesScreen(
                onInstitutionClick = onInstitutionClick,
                modifier = Modifier.padding(innerPadding),
            )
            MainTab.Profile -> ProfileScreen(
                userEmail = userEmail,
                isAdmin = isAdmin,
                favoritesCount = if (isAdmin) 0 else catalogState.favoriteIds.size,
                institutionsCount = catalogState.allInstitutions.size,
                onSignOut = onSignOut,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
