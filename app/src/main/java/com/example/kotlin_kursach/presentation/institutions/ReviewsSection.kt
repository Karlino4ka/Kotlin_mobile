package com.example.kotlin_kursach.presentation.institutions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlin_kursach.domain.model.Review
import com.example.kotlin_kursach.presentation.components.InstitutionRatingBadge
import com.example.kotlin_kursach.presentation.components.RatingPicker

@Composable
fun ReviewsSection(
    averageRating: Double?,
    reviewCount: Int,
    reviewsState: ReviewsUiState,
    onRatingChange: (Int) -> Unit,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Отзывы",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        InstitutionRatingBadge(
            averageRating = averageRating,
            reviewCount = reviewCount,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Ваш отзыв",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text("Оценка", style = MaterialTheme.typography.labelLarge)
                RatingPicker(
                    selectedRating = reviewsState.ratingInput,
                    onRatingSelected = onRatingChange,
                )
                OutlinedTextField(
                    value = reviewsState.textInput,
                    onValueChange = onTextChange,
                    label = { Text("Комментарий") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                reviewsState.submitError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = onSubmit,
                    enabled = reviewsState.canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (reviewsState.isSubmitting) {
                        CircularProgressIndicator()
                    } else {
                        Text("Отправить отзыв")
                    }
                }
            }
        }

        when {
            reviewsState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
            reviewsState.errorMessage != null -> {
                Text(
                    text = reviewsState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            reviewsState.reviews.isEmpty() -> {
                Text(
                    text = "Пока нет отзывов. Будьте первым!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            else -> {
                reviewsState.reviews.forEach { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = review.authorName ?: review.authorEmail,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Оценка: ${review.rating}/5",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

data class ReviewsUiState(
    val isLoading: Boolean = true,
    val reviews: List<Review> = emptyList(),
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val ratingInput: Int = 5,
    val textInput: String = "",
) {
    val canSubmit: Boolean
        get() = textInput.isNotBlank() && !isSubmitting
}
