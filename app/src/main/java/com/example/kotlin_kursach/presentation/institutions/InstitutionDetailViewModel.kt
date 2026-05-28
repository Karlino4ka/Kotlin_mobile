package com.example.kotlin_kursach.presentation.institutions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.SubmitReviewInput
import com.example.kotlin_kursach.domain.repository.FavoriteRepository
import com.example.kotlin_kursach.domain.repository.InstitutionRepository
import com.example.kotlin_kursach.domain.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val reviewRepository: ReviewRepository,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val institutionId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<InstitutionDetailUiState>(InstitutionDetailUiState.Loading)
    val uiState: StateFlow<InstitutionDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavoriteState: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _reviewsState = MutableStateFlow(ReviewsUiState())
    val reviewsState: StateFlow<ReviewsUiState> = _reviewsState.asStateFlow()

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
        loadReviews()
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

    fun loadReviews() {
        viewModelScope.launch {
            _reviewsState.update { it.copy(isLoading = true, errorMessage = null) }
            reviewRepository.getReviews(institutionId)
                .onSuccess { reviews ->
                    val myReview = firebaseAuth.currentUser?.uid?.let { userId ->
                        reviews.find { it.userId == userId }
                    }
                    _reviewsState.update { state ->
                        state.copy(
                            isLoading = false,
                            reviews = reviews,
                            ratingInput = myReview?.rating ?: state.ratingInput,
                            textInput = myReview?.text ?: state.textInput,
                        )
                    }
                }
                .onFailure { error ->
                    _reviewsState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Не удалось загрузить отзывы",
                        )
                    }
                }
        }
    }

    fun updateReviewRating(rating: Int) {
        _reviewsState.update { it.copy(ratingInput = rating, submitError = null) }
    }

    fun updateReviewText(text: String) {
        _reviewsState.update { it.copy(textInput = text, submitError = null) }
    }

    fun submitReview() {
        val state = _reviewsState.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _reviewsState.update { it.copy(isSubmitting = true, submitError = null) }
            reviewRepository.submitReview(
                institutionId = institutionId,
                input = SubmitReviewInput(
                    rating = state.ratingInput,
                    text = state.textInput.trim(),
                ),
            )
                .onSuccess {
                    _reviewsState.update { it.copy(isSubmitting = false) }
                    loadReviews()
                    refreshInstitutionQuietly()
                }
                .onFailure { error ->
                    _reviewsState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.message ?: "Не удалось отправить отзыв",
                        )
                    }
                }
        }
    }

    private fun refreshInstitutionQuietly() {
        viewModelScope.launch {
            repository.getInstitution(institutionId)
                .onSuccess { data ->
                    val current = _uiState.value
                    if (current is InstitutionDetailUiState.Success) {
                        _uiState.value = current.copy(
                            institution = data.value,
                            fromCache = data.fromCache,
                        )
                    }
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(institutionId)
        }
    }

    fun deleteInstitution(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.deleteInstitution(institutionId))
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
