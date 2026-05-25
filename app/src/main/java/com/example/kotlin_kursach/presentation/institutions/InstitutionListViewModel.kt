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

sealed interface InstitutionListUiState {
    data object Loading : InstitutionListUiState
    data class Success(
        val institutions: List<Institution>,
        val fromCache: Boolean = false,
    ) : InstitutionListUiState
    data class Error(val message: String) : InstitutionListUiState
}

class InstitutionListViewModel(
    private val repository: InstitutionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstitutionListUiState>(InstitutionListUiState.Loading)
    val uiState: StateFlow<InstitutionListUiState> = _uiState.asStateFlow()

    init {
        loadInstitutions()
    }

    fun loadInstitutions() {
        viewModelScope.launch {
            val cached = repository.getCachedInstitutions()
            if (cached.isNotEmpty()) {
                _uiState.value = InstitutionListUiState.Success(
                    institutions = cached,
                    fromCache = true,
                )
            } else {
                _uiState.value = InstitutionListUiState.Loading
            }

            repository.getInstitutions()
                .onSuccess { data ->
                    _uiState.value = InstitutionListUiState.Success(
                        institutions = data.value,
                        fromCache = data.fromCache,
                    )
                }
                .onFailure { error ->
                    if (cached.isEmpty()) {
                        _uiState.value = InstitutionListUiState.Error(
                            error.message ?: "Не удалось загрузить список",
                        )
                    }
                }
        }
    }
}

class InstitutionListViewModelFactory(
    private val repository: InstitutionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstitutionListViewModel::class.java)) {
            return InstitutionListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
