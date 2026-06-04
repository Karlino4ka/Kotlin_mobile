package com.example.kotlin_kursach.presentation.institutions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.presentation.components.InstitutionPhotosCarousel
import com.example.kotlin_kursach.presentation.components.InstitutionRatingBadge
import com.example.kotlin_kursach.presentation.components.InstitutionOrientationBadges
import com.example.kotlin_kursach.presentation.components.InstitutionTypeBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionDetailScreen(
    onBack: () -> Unit,
    isAdmin: Boolean = false,
    onEdit: (String) -> Unit = {},
    onDeleted: () -> Unit = {},
    viewModel: InstitutionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavoriteState.collectAsStateWithLifecycle()
    val reviewsState by viewModel.reviewsState.collectAsStateWithLifecycle()
    val showFavorite = !isAdmin
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Учебное заведение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (showFavorite) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            viewModel.shareText()?.let { text ->
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        },
                                        "Поделиться",
                                    ),
                                )
                            }
                        },
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            InstitutionDetailUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            is InstitutionDetailUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::loadInstitution,
                modifier = Modifier.padding(innerPadding),
            )
            is InstitutionDetailUiState.Success -> Column(Modifier.padding(innerPadding)) {
                if (state.fromCache) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            "Офлайн: сохранённые данные",
                            modifier = Modifier.padding(16.dp, 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                InstitutionDetailContent(
                    institution = state.institution,
                    reviewsState = reviewsState,
                    isAdmin = isAdmin,
                    isDeleting = isDeleting,
                    onRatingChange = viewModel::updateReviewRating,
                    onReviewTextChange = viewModel::updateReviewText,
                    onSubmitReview = viewModel::submitReview,
                    onCall = { phone ->
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                    },
                    onOpenWebsite = { url ->
                        val normalized = if (url.startsWith("http")) url else "https://$url"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
                    },
                    onEdit = { onEdit(state.institution.id) },
                    onDeleteClick = { showDeleteDialog = true },
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) showDeleteDialog = false
            },
            title = { Text("Удалить заведение?") },
            text = { Text("Запись будет удалена из каталога для всех пользователей.") },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        deleteError = null
                        viewModel.deleteInstitution { result ->
                            isDeleting = false
                            result.onSuccess {
                                showDeleteDialog = false
                                onDeleted()
                            }.onFailure { error ->
                                deleteError = error.message ?: "Не удалось удалить"
                            }
                        }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(if (isDeleting) "Удаление…" else "Удалить")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeleting,
                ) {
                    Text("Отмена")
                }
            },
        )
    }

    deleteError?.let { message ->
        AlertDialog(
            onDismissRequest = { deleteError = null },
            title = { Text("Ошибка") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { deleteError = null }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun InstitutionDetailContent(
    institution: Institution,
    reviewsState: ReviewsUiState,
    isAdmin: Boolean,
    isDeleting: Boolean,
    onRatingChange: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit,
    onSubmitReview: () -> Unit,
    onCall: (String) -> Unit,
    onOpenWebsite: (String) -> Unit,
    onEdit: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InstitutionPhotosCarousel(
            photos = institution.photos,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = institution.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        InstitutionTypeBadge(type = institution.type)
        InstitutionOrientationBadges(orientations = institution.orientations)
        InstitutionRatingBadge(
            averageRating = institution.averageRating,
            reviewCount = institution.reviewCount,
        )
        InfoCard(title = "Город", value = institution.city)
        InfoCard(title = "Адрес", value = institution.address)
        InfoCard(title = "Описание", value = institution.description)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            institution.phone?.let { phone ->
                FilledTonalButton(
                    onClick = { onCall(phone) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text("Позвонить", modifier = Modifier.padding(start = 6.dp))
                }
            }
            institution.website?.let { site ->
                OutlinedButton(
                    onClick = { onOpenWebsite(site) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = null)
                    Text("Сайт", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
        Text(
            text = institution.type.toDisplayName(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ReviewsSection(
            averageRating = institution.averageRating,
            reviewCount = institution.reviewCount,
            reviewsState = reviewsState,
            onRatingChange = onRatingChange,
            onTextChange = onReviewTextChange,
            onSubmit = onSubmitReview,
        )
        if (isAdmin) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = onEdit,
                    enabled = !isDeleting,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Text("Изменить", modifier = Modifier.padding(start = 6.dp))
                }
                OutlinedButton(
                    onClick = onDeleteClick,
                    enabled = !isDeleting,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Text("Удалить", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Повторить") }
    }
}
