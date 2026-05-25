package com.example.kotlin_kursach.presentation.institutions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface InstitutionDetailUiState {
    data object Loading : InstitutionDetailUiState
    data class Success(
        val institution: Institution,
        val fromCache: Boolean = false,
    ) : InstitutionDetailUiState
    data class Error(val message: String) : InstitutionDetailUiState
}

class InstitutionDetailViewModel(
    private val repository: InstitutionRepository,
    private val institutionId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstitutionDetailUiState>(InstitutionDetailUiState.Loading)
    val uiState: StateFlow<InstitutionDetailUiState> = _uiState.asStateFlow()

    init {
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
                    )
                }
                .onFailure { error ->
                    _uiState.value = InstitutionDetailUiState.Error(
                        error.message ?: "Не удалось загрузить заведение",
                    )
                }
        }
    }
}

class InstitutionDetailViewModelFactory(
    private val repository: InstitutionRepository,
    private val institutionId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstitutionDetailViewModel::class.java)) {
            return InstitutionDetailViewModel(repository, institutionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
