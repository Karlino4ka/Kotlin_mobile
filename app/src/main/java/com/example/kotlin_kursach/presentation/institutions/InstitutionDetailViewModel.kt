package com.example.kotlin_kursach.presentation.institutions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InstitutionDetailUiState {
    data object Loading : InstitutionDetailUiState
    data class Success(
        val institution: Institution,
        val fromCache: Boolean = false,
        val isFavorite: Boolean = false,
    ) : InstitutionDetailUiState
    data class Error(val message: String) : InstitutionDetailUiState
}

@HiltViewModel
class InstitutionDetailViewModel @Inject constructor(
    private val repository: InstitutionRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val institutionId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<InstitutionDetailUiState>(InstitutionDetailUiState.Loading)
    val uiState: StateFlow<InstitutionDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavoriteState: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRepository.observeFavoriteIds().collect { ids ->
                _isFavorite.value = institutionId in ids
                val current = _uiState.value
                if (current is InstitutionDetailUiState.Success) {
                    _uiState.value = current.copy(isFavorite = institutionId in ids)
                }
            }
        }
        loadInstitution()
    }

    fun loadInstitution() {
        viewModelScope.launch {
            _uiState.value = InstitutionDetailUiState.Loading
            repository.getInstitution(institutionId)
                .onSuccess { data ->
                    _uiState.value = InstitutionDetailUiState.Success(
                        institution = data.value,
                        fromCache = data.fromCache,
                        isFavorite = favoriteRepository.isFavorite(institutionId),
                    )
                    _isFavorite.value = favoriteRepository.isFavorite(institutionId)
                }
                .onFailure { error ->
                    _uiState.value = InstitutionDetailUiState.Error(
                        error.message ?: "Не удалось загрузить заведение",
                    )
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(institutionId)
        }
    }

    fun shareText(): String? {
        val state = _uiState.value as? InstitutionDetailUiState.Success ?: return null
        val i = state.institution
        return buildString {
            appendLine(i.name)
            appendLine("${i.type} · ${i.city}")
            appendLine(i.address)
            appendLine(i.description)
            i.phone?.let { appendLine("Тел: $it") }
            i.website?.let { appendLine("Сайт: $it") }
        }
    }
}
