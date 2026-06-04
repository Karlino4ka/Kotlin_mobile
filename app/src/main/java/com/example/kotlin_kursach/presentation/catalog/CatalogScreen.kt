package com.example.kotlin_kursach.presentation.catalog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.domain.model.InstitutionOrientation
import com.example.kotlin_kursach.domain.model.InstitutionSortOrder
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.presentation.components.InstitutionCard
import com.example.kotlin_kursach.presentation.institutions.InstitutionListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onInstitutionClick: (String) -> Unit,
    isAdmin: Boolean,
    onAddInstitution: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InstitutionListViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddInstitution) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить заведение")
                }
            }
        },
    ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        if (state.fromCache) {
            OfflineBanner()
        }
        SearchAndFilters(
            searchQuery = state.filters.searchQuery,
            selectedType = state.filters.type,
            selectedOrientation = state.filters.orientation,
            selectedCity = state.filters.city,
            cities = state.availableCities,
            statsText = state.statsText,
            onSearchChange = viewModel::onSearchQueryChange,
            onTypeChange = viewModel::onTypeFilterChange,
            onOrientationChange = viewModel::onOrientationFilterChange,
            onCityChange = viewModel::onCityFilterChange,
            onClearFilters = viewModel::clearFilters,
            onSortClick = { sortMenuExpanded = true },
        )
        SortMenu(
            expanded = sortMenuExpanded,
            onDismiss = { sortMenuExpanded = false },
            current = state.filters.sortOrder,
            onSelect = {
                viewModel.onSortOrderChange(it)
                sortMenuExpanded = false
            },
        )

        when {
            state.isLoading -> LoadingBox()
            state.errorMessage != null && state.allInstitutions.isEmpty() -> ErrorBox(
                message = state.errorMessage!!,
                onRetry = { viewModel.loadInstitutions(showFullScreenLoading = true) },
            )
            state.filteredInstitutions.isEmpty() -> EmptyBox(
                message = if (state.allInstitutions.isEmpty()) {
                    "Список пуст. Запустите сервер и обновите."
                } else {
                    "Ничего не найдено. Измените фильтры."
                },
            )
            else -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.loadInstitutions() },
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.filteredInstitutions, key = { it.id }) { institution ->
                        InstitutionCard(
                            institution = institution,
                            onClick = { onInstitutionClick(institution.id) },
                            showFavorite = !isAdmin,
                            isFavorite = institution.id in state.favoriteIds,
                            onFavoriteClick = { viewModel.toggleFavorite(institution.id) },
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun SearchAndFilters(
    searchQuery: String,
    selectedType: InstitutionType?,
    selectedOrientation: InstitutionOrientation?,
    selectedCity: String?,
    cities: List<String>,
    statsText: String,
    onSearchChange: (String) -> Unit,
    onTypeChange: (InstitutionType?) -> Unit,
    onOrientationChange: (InstitutionOrientation?) -> Unit,
    onCityChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onSortClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statsText,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onSortClick) {
                Icon(Icons.Default.Sort, contentDescription = "Сортировка")
            }
            if (searchQuery.isNotEmpty() || selectedType != null || selectedOrientation != null || selectedCity != null) {
                IconButton(onClick = onClearFilters) {
                    Icon(Icons.Default.Clear, contentDescription = "Сбросить фильтры")
                }
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            placeholder = { Text("Поиск по названию, городу...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeChange(null) },
                label = { Text("Все типы") },
            )
            InstitutionType.entries.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeChange(if (selectedType == type) null else type) },
                    label = { Text(type.toDisplayName()) },
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedOrientation == null,
                onClick = { onOrientationChange(null) },
                label = { Text("Все направленности") },
            )
            InstitutionOrientation.entries.forEach { orientation ->
                FilterChip(
                    selected = selectedOrientation == orientation,
                    onClick = {
                        onOrientationChange(if (selectedOrientation == orientation) null else orientation)
                    },
                    label = { Text(orientation.toDisplayName()) },
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = selectedCity == null,
                onClick = { onCityChange(null) },
                label = { Text("Все города") },
            )
            cities.forEach { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick = { onCityChange(if (selectedCity == city) null else city) },
                    label = { Text(city) },
                )
            }
        }
    }
}

@Composable
private fun SortMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    current: InstitutionSortOrder,
    onSelect: (InstitutionSortOrder) -> Unit,
) {
    Box {
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            DropdownMenuItem(
                text = { Text("По названию") },
                onClick = { onSelect(InstitutionSortOrder.NAME_ASC) },
                leadingIcon = {
                    if (current == InstitutionSortOrder.NAME_ASC) {
                        Text("✓")
                    }
                },
            )
            DropdownMenuItem(
                text = { Text("По городу") },
                onClick = { onSelect(InstitutionSortOrder.CITY_ASC) },
            )
            DropdownMenuItem(
                text = { Text("По типу") },
                onClick = { onSelect(InstitutionSortOrder.TYPE) },
            )
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(
            text = "Офлайн: показаны сохранённые данные",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

@Composable
private fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message)
        androidx.compose.material3.Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Повторить")
        }
    }
}

@Composable
private fun EmptyBox(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}
