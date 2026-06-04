package com.example.kotlin_kursach.presentation.institutions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionFilters
import com.example.kotlin_kursach.domain.model.InstitutionOrientation
import com.example.kotlin_kursach.domain.model.InstitutionSortOrder
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.model.applyFilters
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogScreenState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val fromCache: Boolean = false,
    val allInstitutions: List<Institution> = emptyList(),
    val filters: InstitutionFilters = InstitutionFilters(),
    val favoriteIds: Set<String> = emptySet(),
) {
    val filteredInstitutions: List<Institution> =
        allInstitutions.applyFilters(filters)

    val availableCities: List<String> =
        allInstitutions.map { it.city }.distinct().sorted()

    val statsText: String
        get() {
            val total = allInstitutions.size
            val shown = filteredInstitutions.size
            return if (shown == total) {
                "$total заведений"
            } else {
                "Показано $shown из $total"
            }
        }
}

@HiltViewModel
class InstitutionListViewModel @Inject constructor(
    private val repository: InstitutionRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow(CatalogScreenState())
    val screenState: StateFlow<CatalogScreenState> = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRepository.observeFavoriteIds().collect { ids ->
                _screenState.update { it.copy(favoriteIds = ids) }
            }
        }
        loadInstitutions(showFullScreenLoading = true)
    }

    fun loadInstitutions(showFullScreenLoading: Boolean = false) {
        viewModelScope.launch {
            if (showFullScreenLoading) {
                _screenState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _screenState.update { it.copy(isRefreshing = true, errorMessage = null) }
            }

            val cached = repository.getCachedInstitutions()
            if (cached.isNotEmpty()) {
                _screenState.update {
                    it.copy(
                        isLoading = false,
                        allInstitutions = cached,
                        fromCache = true,
                    )
                }
            }

            repository.getInstitutions()
                .onSuccess { data ->
                    _screenState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            allInstitutions = data.value,
                            fromCache = data.fromCache,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _screenState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = if (it.allInstitutions.isEmpty()) {
                                error.message ?: "Не удалось загрузить список"
                            } else {
                                null
                            },
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _screenState.update {
            it.copy(filters = it.filters.copy(searchQuery = query))
        }
    }

    fun onTypeFilterChange(type: InstitutionType?) {
        _screenState.update {
            it.copy(filters = it.filters.copy(type = type))
        }
    }

    fun onCityFilterChange(city: String?) {
        _screenState.update {
            it.copy(filters = it.filters.copy(city = city))
        }
    }

    fun onOrientationFilterChange(orientation: InstitutionOrientation?) {
        _screenState.update {
            it.copy(filters = it.filters.copy(orientation = orientation))
        }
    }

    fun onSortOrderChange(sortOrder: InstitutionSortOrder) {
        _screenState.update {
            it.copy(filters = it.filters.copy(sortOrder = sortOrder))
        }
    }

    fun clearFilters() {
        _screenState.update {
            it.copy(filters = InstitutionFilters())
        }
    }

    fun toggleFavorite(institutionId: String) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(institutionId)
        }
    }
}
