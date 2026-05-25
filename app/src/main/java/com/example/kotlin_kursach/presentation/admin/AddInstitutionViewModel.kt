package com.example.kotlin_kursach.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddInstitutionFormState(
    val name: String = "",
    val type: InstitutionType = InstitutionType.SCHOOL,
    val city: String = "",
    val address: String = "",
    val description: String = "",
    val phone: String = "",
    val website: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
) {
    val canSubmit: Boolean
        get() = name.isNotBlank() && city.isNotBlank() && address.isNotBlank() &&
            description.isNotBlank() && !isSubmitting
}

@HiltViewModel
class AddInstitutionViewModel @Inject constructor(
    private val repository: InstitutionRepository,
) : ViewModel() {

    private val _formState = MutableStateFlow(AddInstitutionFormState())
    val formState: StateFlow<AddInstitutionFormState> = _formState.asStateFlow()

    fun updateName(value: String) = _formState.update { it.copy(name = value, errorMessage = null) }
    fun updateCity(value: String) = _formState.update { it.copy(city = value, errorMessage = null) }
    fun updateAddress(value: String) = _formState.update { it.copy(address = value, errorMessage = null) }
    fun updateDescription(value: String) = _formState.update { it.copy(description = value, errorMessage = null) }
    fun updatePhone(value: String) = _formState.update { it.copy(phone = value) }
    fun updateWebsite(value: String) = _formState.update { it.copy(website = value) }
    fun updateType(value: InstitutionType) = _formState.update { it.copy(type = value) }

    fun submit() {
        val state = _formState.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null) }
            repository.createInstitution(
                CreateInstitutionInput(
                    name = state.name.trim(),
                    type = state.type,
                    city = state.city.trim(),
                    address = state.address.trim(),
                    description = state.description.trim(),
                    phone = state.phone.trim().ifBlank { null },
                    website = state.website.trim().ifBlank { null },
                ),
            )
                .onSuccess {
                    _formState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "Не удалось добавить заведение",
                        )
                    }
                }
        }
    }
}
