package com.example.kotlin_kursach.presentation.admin

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.InstitutionPhoto
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class InstitutionFormState(
    val isLoading: Boolean = false,
    val name: String = "",
    val type: InstitutionType = InstitutionType.SCHOOL,
    val city: String = "",
    val address: String = "",
    val description: String = "",
    val phone: String = "",
    val website: String = "",
    val photos: List<InstitutionPhoto> = emptyList(),
    val localPhotoUris: List<String> = emptyList(),
    val photoUrlInput: String = "",
    val isPhotoProcessing: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
) {
    val displayPhotos: List<InstitutionPhoto>
        get() = photos + localPhotoUris.map { uri ->
            InstitutionPhoto(id = uri, url = uri)
        }

    val canSubmit: Boolean
        get() = name.isNotBlank() && city.isNotBlank() && address.isNotBlank() &&
            description.isNotBlank() && !isSubmitting && !isLoading && !isPhotoProcessing
}

@HiltViewModel
class InstitutionFormViewModel @Inject constructor(
    private val repository: InstitutionRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val institutionId: String? = savedStateHandle.get<String>("id")
    val isEditMode: Boolean = institutionId != null

    private val _formState = MutableStateFlow(InstitutionFormState())
    val formState: StateFlow<InstitutionFormState> = _formState.asStateFlow()

    init {
        institutionId?.let { loadInstitution(it) }
    }

    private fun loadInstitution(id: String) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getInstitution(id)
                .onSuccess { data ->
                    val institution = data.value
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            name = institution.name,
                            type = institution.type,
                            city = institution.city,
                            address = institution.address,
                            description = institution.description,
                            phone = institution.phone.orEmpty(),
                            website = institution.website.orEmpty(),
                            photos = institution.photos,
                        )
                    }
                }
                .onFailure { error ->
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Не удалось загрузить заведение",
                        )
                    }
                }
        }
    }

    fun updateName(value: String) = _formState.update { it.copy(name = value, errorMessage = null) }
    fun updateCity(value: String) = _formState.update { it.copy(city = value, errorMessage = null) }
    fun updateAddress(value: String) = _formState.update { it.copy(address = value, errorMessage = null) }
    fun updateDescription(value: String) = _formState.update { it.copy(description = value, errorMessage = null) }
    fun updatePhone(value: String) = _formState.update { it.copy(phone = value) }
    fun updateWebsite(value: String) = _formState.update { it.copy(website = value) }
    fun updateType(value: InstitutionType) = _formState.update { it.copy(type = value) }
    fun updatePhotoUrlInput(value: String) = _formState.update { it.copy(photoUrlInput = value) }

    fun addPhotoByUrl() {
        val url = _formState.value.photoUrlInput.trim()
        if (url.isBlank()) return

        if (isEditMode) {
            val id = institutionId ?: return
            viewModelScope.launch {
                _formState.update { it.copy(isPhotoProcessing = true, errorMessage = null) }
                repository.addPhotoUrl(id, url)
                    .onSuccess { photo ->
                        _formState.update {
                            it.copy(
                                isPhotoProcessing = false,
                                photos = it.photos + photo,
                                photoUrlInput = "",
                            )
                        }
                    }
                    .onFailure { error ->
                        _formState.update {
                            it.copy(
                                isPhotoProcessing = false,
                                errorMessage = error.message ?: "Не удалось добавить фото",
                            )
                        }
                    }
            }
        } else {
            _formState.update {
                it.copy(
                    localPhotoUris = it.localPhotoUris + url,
                    photoUrlInput = "",
                )
            }
        }
    }

    fun onPhotoPicked(uri: Uri) {
        if (isEditMode) {
            val id = institutionId ?: return
            viewModelScope.launch {
                _formState.update { it.copy(isPhotoProcessing = true, errorMessage = null) }
                uploadUri(id, uri)
                    .onSuccess { photo ->
                        _formState.update {
                            it.copy(
                                isPhotoProcessing = false,
                                photos = it.photos + photo,
                            )
                        }
                    }
                    .onFailure { error ->
                        _formState.update {
                            it.copy(
                                isPhotoProcessing = false,
                                errorMessage = error.message ?: "Не удалось загрузить фото",
                            )
                        }
                    }
            }
        } else {
            _formState.update {
                it.copy(localPhotoUris = it.localPhotoUris + uri.toString())
            }
        }
    }

    fun removePhoto(photoId: String) {
        val state = _formState.value
        if (state.localPhotoUris.contains(photoId)) {
            _formState.update {
                it.copy(localPhotoUris = it.localPhotoUris.filterNot { uri -> uri == photoId })
            }
            return
        }

        if (!isEditMode) return
        val id = institutionId ?: return
        viewModelScope.launch {
            _formState.update { it.copy(isPhotoProcessing = true, errorMessage = null) }
            repository.deletePhoto(id, photoId)
                .onSuccess {
                    _formState.update {
                        it.copy(
                            isPhotoProcessing = false,
                            photos = it.photos.filterNot { photo -> photo.id == photoId },
                        )
                    }
                }
                .onFailure { error ->
                    _formState.update {
                        it.copy(
                            isPhotoProcessing = false,
                            errorMessage = error.message ?: "Не удалось удалить фото",
                        )
                    }
                }
        }
    }

    fun submit() {
        val state = _formState.value
        if (!state.canSubmit) return

        val input = CreateInstitutionInput(
            name = state.name.trim(),
            type = state.type,
            city = state.city.trim(),
            address = state.address.trim(),
            description = state.description.trim(),
            phone = state.phone.trim().ifBlank { null },
            website = state.website.trim().ifBlank { null },
        )

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = if (isEditMode) {
                repository.updateInstitution(institutionId!!, input)
            } else {
                repository.createInstitution(input)
            }
            result
                .onSuccess { institution ->
                    if (!isEditMode) {
                        syncPendingPhotos(institution.id)
                    } else {
                        _formState.update { it.copy(isSubmitting = false, isSuccess = true) }
                    }
                }
                .onFailure { error ->
                    val defaultMessage = if (isEditMode) {
                        "Не удалось сохранить изменения"
                    } else {
                        "Не удалось добавить заведение"
                    }
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: defaultMessage,
                        )
                    }
                }
        }
    }

    private suspend fun syncPendingPhotos(institutionId: String) {
        val pending = _formState.value.localPhotoUris
        if (pending.isEmpty()) {
            _formState.update { it.copy(isSubmitting = false, isSuccess = true) }
            return
        }

        _formState.update { it.copy(isPhotoProcessing = true) }
        var error: String? = null
        for (entry in pending) {
            val result = if (entry.startsWith("http://") || entry.startsWith("https://")) {
                repository.addPhotoUrl(institutionId, entry)
            } else {
                uploadUri(institutionId, Uri.parse(entry))
            }
            if (result.isFailure) {
                error = result.exceptionOrNull()?.message ?: "Не удалось загрузить фото"
                break
            }
        }
        _formState.update {
            it.copy(
                isSubmitting = false,
                isPhotoProcessing = false,
                isSuccess = error == null,
                errorMessage = error,
                localPhotoUris = if (error == null) emptyList() else it.localPhotoUris,
            )
        }
    }

    private suspend fun uploadUri(institutionId: String, uri: Uri): Result<InstitutionPhoto> {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return Result.failure(IllegalStateException("Не удалось прочитать файл"))
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?: "jpg"
        val fileName = "photo_${UUID.randomUUID()}.$extension"
        return repository.uploadPhoto(institutionId, fileName, bytes, mimeType)
    }
}
