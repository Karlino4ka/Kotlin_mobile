package com.example.kotlin_kursach.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val institutionRepository: InstitutionRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val allInstitutions = MutableStateFlow<List<Institution>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val institutions: StateFlow<List<Institution>> = combine(
        allInstitutions,
        favoriteRepository.observeFavoriteIds(),
    ) { institutions, favoriteIds ->
        institutions.filter { it.id in favoriteIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val cached = institutionRepository.getCachedInstitutions()
            if (cached.isNotEmpty()) {
                allInstitutions.value = cached
            }
            institutionRepository.getInstitutions()
                .onSuccess { data -> allInstitutions.value = data.value }
            _isLoading.value = false
        }
    }

    fun toggleFavorite(institutionId: String) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(institutionId)
        }
    }
}
